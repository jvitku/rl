package org.hanns.rl.discrete.ros.sarsa;

import org.apache.commons.logging.Log;
import org.hanns.rl.common.exceptions.MessageFormatException;
import org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.config.impl.BasicConfig;
import org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.impl.EpsilonGreedyDouble;
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
	public final String s = "/";
	public static final String actionPrefix = "a";	// action names: a0, a1,a2,..
	public static final String statePrefix = "s"; 	// state var. names: s0,s1,..

	public static final String dataIn = Topic.baseIn+"States"; 		// inStates
	public static final String dataOut = Topic.baseOut+"Actions"; 	// outActions

	private PrivateRosparam r;

	/**
	 * Learning rate
	 */
	public static final String alphaConfig = "alpha";
	public static final double DEF_ALPHA = 0.7;

	/**
	 * Decay factor
	 */
	public static final String gammaConfig = "gamma";
	public static final double DEF_GAMMA = 0.4;

	/**
	 * Trace decay factor
	 */
	public static final String lambdaConfig = "lambda";
	public static final double DEF_LAMBDA = 0.4;
	public static final String traceLength = "traceLength";
	public static final int DEF_TRACELEN = 20;

	/**
	 * TODO: parameter (input?) rl enabled
	 */

	/**
	 * Number of state variables considered by the RL (predefined sampling)
	 */
	public static final String noInputs = "noInputs";
	public static final int DEF_STATEVARS = 2;

	/**
	 * Number of actions that can be performed by the RL ASM (coding 1ofN)
	 */
	public static final String noOutputs = "noOutputs";
	public static final int DEF_NOACTIONS = 2;

	/**
	 * Default sampling parameters, TODO: customize each variable sampling independently
	 * 
	 * Sampling is from the interval [{@link #sampleMn}, {@link #sampleMx}] with 
	 * {@link #sampleC} of samples.
	 */
	public static final String sampleMin="sampleMin", sampleMax="sampleMax", sampleCount="sampleCount";
	public static final double DEF_MIN=0, DEF_MAX=1;
	public static final int DEF_COUNT=5;

	/**
	 * Epsilon-greedy ASM configuration
	 */
	public static final String epsilonConfig="epsilon";
	public static final double DEF_EPSILON=0.6;

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

	//private double currentReward;			// reward just received from the node	
	//private float[] currentState;			

	private int prevAction;					// index of the last action executed
	
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

		// encode the raw float[] values into state variables
		try {
			states.setRawData(state);
		} catch (MessageFormatException e) {
			e.printStackTrace();
			log.error(me+"Could not encode state description into state variables");
		}
		// select action, perform learning step
		int action = asm.selectAction(q.getActionValsInState(states.getValues()));
		rl.performLearningStep(prevAction, reward, states.getValues(), action);

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

		this.myLog(me+"parsing parameters");

		// RL parameters (default alpha and gamma, but can be also modified online)
		double alpha = r.getMyDouble(alphaConfig, DEF_ALPHA);
		double gamma = r.getMyDouble(gammaConfig, DEF_GAMMA);
		double lambda = r.getMyDouble(lambdaConfig, DEF_LAMBDA);
		int len = r.getMyInteger(traceLength, DEF_TRACELEN);
		double epsilon = r.getMyDouble(epsilonConfig, DEF_EPSILON);

		// dimensionality of the rl task 
		int noStateVars = r.getMyInteger(noInputs, DEF_STATEVARS);
		int noActions = r.getMyInteger(noOutputs, DEF_NOACTIONS);

		// configuration of sampling for state variables (float->finite no. of states) 
		double sampleMn = r.getMyDouble(sampleMin, DEF_MIN);
		double sampleMx = r.getMyDouble(sampleMax, DEF_MAX);
		int sampleC = r.getMyInteger(sampleCount, DEF_COUNT);

		this.myLog(me+"creating data structures");

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
		actionPublisher =connectedNode.newPublisher(dataOut, std_msgs.Float32MultiArray._TYPE);

		/**
		 * State receiver
		 */
		Subscriber<std_msgs.Float32MultiArray> epsilonSub = 
				connectedNode.newSubscriber(dataIn, std_msgs.Float32MultiArray._TYPE);

		epsilonSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error(me+"-"+dataIn+" Received state description has" +
							"unexpected length of"+data.length+"! Expected: "+
							states.getNumVariables());
				else{
					// here, the state description is decoded and one SARSA step executed
					myLog(me+"-"+dataIn+" Received new reinforcement & state description "+SL.toStr(data));

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
				connectedNode.newSubscriber(name+s+epsilonConfig, std_msgs.Float32MultiArray._TYPE);

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
				connectedNode.newSubscriber(name+s+alphaConfig, std_msgs.Float32MultiArray._TYPE);

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
				connectedNode.newSubscriber(name+s+gammaConfig, std_msgs.Float32MultiArray._TYPE);

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
				connectedNode.newSubscriber(name+s+lambdaConfig, std_msgs.Float32MultiArray._TYPE);

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
