package org.hanns.rl.discrete.ros.sarsa;

import org.apache.commons.logging.Log;
import org.hanns.rl.common.exceptions.MessageFormatException;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.BasicConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.EpsilonGreedyDouble;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.actions.impl.OneOfNEncoder;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.impl.FinalModelNStepQLambda;
import org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.impl.NStepQLambdaConfImpl;
import org.hanns.rl.discrete.ros.Topic;
import org.hanns.rl.discrete.states.impl.BasicFinalStateSet;
import org.hanns.rl.discrete.states.impl.BasicStateVariable;
import org.hanns.rl.discrete.states.impl.BasicVariableEncoder;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.rosparam.impl.PrivateRosparam;
import ctu.nengoros.util.SL;

/**
 * Implementation of Q(lambda) RL algorithm, which is usable a ROS node.
 * 
 * @author Jaroslav Vitku
 *
 */
public class QLambda extends AbstractNodeMain {

	public static final String name = "QLambda";
	public final String me = "["+name+"] ";
	public static final String s = "/";
	
	public static final String ns = name+s; // namespace for config. parameters			
	public static final String actionPrefix = "a";	// action names: a0, a1,a2,..
	public static final String statePrefix = "s"; 	// state var. names: s0,s1,..

	public static final String topicDataIn = Topic.baseIn+"States"; // inStates
	public static final String topicDataOut = Topic.baseOut+"Actions"; // outActions

	private PrivateRosparam r;

	/**
	 * Learning rate
	 */
	public static final String alphaConf = "alpha";
	public static final String topicAlpha= ns+alphaConf;
	public static final double DEF_ALPHA = 0.7;

	/**
	 * Decay factor
	 */
	public static final String gammaConf = "gamma";
	public static final String topicGamma = ns+gammaConf;
	public static final double DEF_GAMMA = 0.4;

	/**
	 * Trace decay factor
	 */
	public static final String lambdaConf = "lambda";
	public static final String topicLambda = ns+lambdaConf;

	public static final double DEF_LAMBDA = 0.4;
	public static final String traceLenConf = "traceLenConf";
	public static final int DEF_TRACELEN = 20;

	/**
	 * TODO: parameter (input?) rl enabled
	 */

	/**
	 * Number of state variables considered by the RL (predefined sampling)
	 */
	public static final String noInputsConf = "noInputs";
	public static final int DEF_STATEVARS = 2;

	/**
	 * Number of actions that can be performed by the RL ASM (coding 1ofN)
	 */
	public static final String noOutputsConf = "noOutputsConf";
	public static final int DEF_NOACTIONS = 4;

	/**
	 * Default sampling parameters, TODO: customize each variable sampling 
	 * independently
	 * 
	 * Sampling is from the interval [{@link #sampleMn}, {@link #sampleMx}] with 
	 * {@link #sampleC} of samples.
	 */
	public static final String sampleMinConf="sampleMin",
			sampleMaxConf="sampleMax", sampleCountConf="sampleCount";

	public static final double DEF_MIN=0, DEF_MAX=1;
	public static final int DEF_COUNT=5;

	/**
	 * Epsilon-greedy ASM configuration
	 */
	public static final String epsilonConf="epsilon";
	public static final String topicEpsilon = ns+epsilonConf;
	public static final double DEF_EPSILON=0.6;

	public static final int DEF_LOGPERIOD =10;	// how often to log? 
	public static final String logPeriodConf = "logPeriod";
	private int logPeriod; 

	/**
	 * ROS node configuration
	 */
	public static final String shouldLog = "shouldLog";
	public static final boolean DEF_LOG = true;
	private boolean willLog = true;
	private Log log;
	private Publisher<std_msgs.Float32MultiArray> actionPublisher;

	/**
	 * RL stuff
	 */
	private FinalModelNStepQLambda rl;		// RL algorithm
	FinalQMatrix<Double> q;					// Q(s,a) matrix used by the RL
	private EpsilonGreedyDouble asm;		// action selection methods

	private OneOfNEncoder actionEncoder;	// encode actions to ROS
	private BasicFinalActionSet actions;	// set of agents actions

	private BasicFinalStateSet states;		// state variables (each has encoder)

	private int prevAction;					// index of the last action executed

	private int step = 0;

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();

		log.info(me+"started, parsing parameters");
		this.parseParameters(connectedNode);

		myLog(me+"initializing ROS Node IO");
		this.buildASMSumbscribers(connectedNode);
		this.buildEligibilitySubscribers(connectedNode);
		this.buildRLSubscribers(connectedNode);
		this.buildDataIO(connectedNode);

		myLog(me+"Node configured and ready now!");
	}

	private void performSARSAstep(float reward, float[] state){

		//SL.sinfol(me+"\n\n my pos: "+SL.toStr(state)+" reward "+reward);
		
		// encode the raw float[] values into state variables
		try {
			states.setRawData(state);
		} catch (MessageFormatException e) {
			log.error(me+"ERROR: Could not encode state description into state variables");
			e.printStackTrace();
		}
		// select action, perform learning step
		int action = asm.selectAction(q.getActionValsInState(states.getValues()));
		rl.performLearningStep(prevAction, reward, states.getValues(), action);

		if((step++)%logPeriod==0) 
			log.info(me+"Step: "+step+"-> responding with the following action: "
					+SL.toStr(actionEncoder.encode(action)));

		// publish action selected by the ASM
		std_msgs.Float32MultiArray fl = actionPublisher.newMessage();	
		fl.setData(actionEncoder.encode(action));								
		actionPublisher.publish(fl);

		prevAction = action;
	}

	/**
	 * Read private parameters potentially passed to the node. 
	 */
	@SuppressWarnings("unchecked")
	private void parseParameters(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		willLog = r.getMyBoolean(shouldLog, DEF_LOG);
		logPeriod = r.getMyInteger(logPeriodConf, DEF_LOGPERIOD);

		this.myLog(me+"parsing parameters");

		// RL parameters (default alpha and gamma, but can be also modified online)
		double alpha = r.getMyDouble(alphaConf, DEF_ALPHA);
		double gamma = r.getMyDouble(gammaConf, DEF_GAMMA);
		double lambda = r.getMyDouble(lambdaConf, DEF_LAMBDA);
		int len = r.getMyInteger(traceLenConf, DEF_TRACELEN);
		double epsilon = r.getMyDouble(epsilonConf, DEF_EPSILON);

		// dimensionality of the RL task 
		int noStateVars = r.getMyInteger(noInputsConf, DEF_STATEVARS);
		int noActions = r.getMyInteger(noOutputsConf, DEF_NOACTIONS);

		// configuration of sampling for state variables (float->finite no. of states) 
		double sampleMn = r.getMyDouble(sampleMinConf, DEF_MIN);
		double sampleMx = r.getMyDouble(sampleMaxConf, DEF_MAX);
		int sampleC = r.getMyInteger(sampleCountConf, DEF_COUNT);

		this.myLog(me+"Creating data structures.");

		/**
		 *  build variable set (each variable has own encoder (shared for now))
		 */
		BasicVariableEncoder enc = new BasicVariableEncoder(sampleMn, sampleMx, sampleC);
		BasicStateVariable[] vars = new BasicStateVariable[noStateVars];
		for(int i=0; i<vars.length; i++){
			vars[i] = new BasicStateVariable(statePrefix+i,enc);
		}
		states = new BasicFinalStateSet(vars);

		/**
		 * build action set & action encoder
		 */
		String[] names = new String[noActions];
		for(int i=0; i<noActions; i++)
			names[i] = actionPrefix+i;
		actions = new BasicFinalActionSet(names);
		actionEncoder = new OneOfNEncoder(actions);

		/**
		 * build the RL algorithm configuration
		 */
		NStepQLambdaConfImpl config = new NStepQLambdaConfImpl(len, lambda);
		config.setAlpha(alpha);
		config.setGamma(gamma);

		/**
		 *  configure the ASM
		 */
		BasicConfig asmConf = new BasicConfig();
		asm = new EpsilonGreedyDouble(actions, asmConf);
		asm.getConfig().setEpsilon(epsilon);
		asm.getConfig().setExplorationEnabled(true);

		/**
		 *  build the RL algorithm and obtain its Q(s,a) matrix
		 */
		rl = new FinalModelNStepQLambda(states, actions.getNumOfActions(), config);
		q = (FinalQMatrix<Double>)(rl.getMatrix());
	}

	private void buildDataIO(ConnectedNode connectedNode){
		/**
		 * Action publisher
		 */
		actionPublisher =connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE);

		/**
		 * State receiver
		 */
		Subscriber<std_msgs.Float32MultiArray> dataSub = 
				connectedNode.newSubscriber(topicDataIn, std_msgs.Float32MultiArray._TYPE);

		dataSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != states.getNumVariables()+1)
					log.error(me+":"+topicDataIn+": Received state description has" +
							"unexpected length of"+data.length+"! Expected: "+
							(states.getNumVariables()+1));
				else{
					// here, the state description is decoded and one SARSA step executed
					if(step%logPeriod==0)
						myLog(me+"<-"+topicDataIn+" Received new reinforcement &" +
								" state description "+SL.toStr(data));

					// decode data (first value is reinforcement..
					// ..the rest are values of state variables
					float reward = data[0];
					float[] state = new float[data.length-1];
					for(int i=0; i<state.length; i++){
						state[i] = data[i+1];
					}
					// perform the SARSA step
					performSARSAstep(reward, state);
				}
			}
		});
	}

	private void buildASMSumbscribers(ConnectedNode connectedNode){
		/**
		 * Epsilon
		 */
		Subscriber<std_msgs.Float32MultiArray> epsilonSub = 
				connectedNode.newSubscriber(name+s+epsilonConf, std_msgs.Float32MultiArray._TYPE);

		epsilonSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Epsilon config: Received message has " +
							"unexpected length of"+data.length+"!");
				else{
					logParamChange("RECEIVED chage of value Epsilon",
							asm.getConfig().getEpsilon(),data[0]);
					asm.getConfig().setEpsilon(data[0]);
				}
			}
		});
	}

	private void buildRLSubscribers(ConnectedNode connectedNode){
		/**
		 * Alpha
		 */
		Subscriber<std_msgs.Float32MultiArray> alphaSub = 
				connectedNode.newSubscriber(name+s+alphaConf, std_msgs.Float32MultiArray._TYPE);

		alphaSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Gamma config: Received message has " +
							"unexpected length of"+data.length+"!");
				else{
					logParamChange("RECEIVED chage of value Alpha",
							rl.getConfig().getAlpha(), data[0]);
					rl.getConfig().setAlpha(data[0]);
				}
			}
		});

		/**
		 * Gamma
		 */
		Subscriber<std_msgs.Float32MultiArray> gammaSub = 
				connectedNode.newSubscriber(name+s+gammaConf, std_msgs.Float32MultiArray._TYPE);

		gammaSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Gamma config: Received message has" +
							" unexpected length of"+data.length+"!");
				else{
					logParamChange("RECEIVED chage of value gamma",
							rl.getConfig().getGamma(), data[0]);
					rl.getConfig().setGamma(data[0]);
				}
			}
		});	
	}

	private void buildEligibilitySubscribers(ConnectedNode connectedNode){
		/**
		 * Lambda
		 */
		Subscriber<std_msgs.Float32MultiArray> lambdaSub = 
				connectedNode.newSubscriber(name+s+lambdaConf, std_msgs.Float32MultiArray._TYPE);

		lambdaSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Lambda config: Received message has" +
							" unexpected length of"+data.length+"!");
				else{
					logParamChange("RECEIVED chage of value lambda",
							rl.getConfig().getLambda(), data[0]);
					rl.getConfig().setLambda(data[0]);
				}
			}
		});
	}

	private void myLog(String what){
		if(this.willLog)
			log.info(what);
	}

	/**
	 * Log only if allowed, and if the value is changed
	 * @param message message to show value change
	 * @param oldVal old value
	 * @param newVal new one
	 */
	private void logParamChange(String message, double oldVal, double newVal){
		if(!this.willLog)
			return;
		if(oldVal==newVal)
			return;
		log.info(message+" Value is being changed from: "+oldVal+" to "+newVal);
	}
}
