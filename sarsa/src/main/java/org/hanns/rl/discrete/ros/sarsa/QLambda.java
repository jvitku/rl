package org.hanns.rl.discrete.ros.sarsa;

import org.apache.commons.logging.Log;
import org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.config.EpsilonGreedyConfig;
import org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.config.impl.BasicConfig;
import org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.impl.EpsilonGreedyDouble;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.actions.impl.OneOfNEncoder;
import org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.impl.FinalModelNStepQLambda;
import org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.impl.NStepQLambdaConfImpl;
import org.hanns.rl.discrete.states.impl.BasicFinalStateSet;
import org.hanns.rl.discrete.states.impl.BasicStateVariable;
import org.hanns.rl.discrete.states.impl.BasicVariableEncoder;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.rosparam.impl.PrivateRosparam;

/**
 * Get vector of 4 floats, select min and max and publish them.
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
	
	private PrivateRosparam r;

	// allow RL parameters as stand alone inputs to the ROS node? (parameter by def.)
	public static final String paramsOnline = "paramsOnline";
	public final boolean DEF_PARAMS = false;
	private boolean paramsOnl;
	/**
	 * Learning rate
	 */
	public static final String alphaConfig = "alpha";
	public static final double DEF_ALPHA = 0.7;
	private double alpha;

	/**
	 * Discount factor
	 */
	public static final String gammaConfig = "gamma";
	public static final double DEF_GAMMA = 0.4;
	//private double gamma;

	/**
	 * Trace discount factor
	 */
	public static final String lambdaConfig = "lambda";
	public static final double DEF_LAMBDA = 0.4;
	//private double lambda;
	public static final String traceLength = "traceLength";
	public static final int DEF_TRACELEN = 20;
	//private int len;
	
	/**
	 * TODO: parameter (input?) learning enabled
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
	//public double sampleMn, sampleMx, sampleC;

	/**
	 * Epsilon-greedy ASM configuration
	 */
	public static final String epsilonConfig="epsilon";
	public static final double DEF_EPSILON=0.6;
	
	
	public static final String shouldLog = "shouldLog";
	public static final boolean DEF_LOG = true;
	private boolean willLog = true;
	private Log log;

	private FinalModelNStepQLambda learning;// learning algorithm
	private EpsilonGreedyDouble asm;		// action selection methods
	
	private OneOfNEncoder actionEncoder;	// encode actions to ROS
	private BasicFinalActionSet actions;	// set of agents actions
	
	//private BasicStateVariable[] vars;		// state variables (each has encoder)
	private BasicFinalStateSet states;		
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();
		log.info(me+"started, parsing parameters");

		this.parseParameters(connectedNode);
		myLog(me+"initializing Node IO");


		myLog(me+"Node configured and ready now!");
		myLog(me+"alpha is: "+((NStepQLambdaConfImpl)learning.getConfig()).getAlpha());
	}




	/**
	 * Read private parameters potentially passed to the node. 
	 */
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
		
		System.out.println("alpha: "+alpha);
		
		paramsOnl = r.getMyBoolean(paramsOnline, DEF_PARAMS);

		// dimensionality of the learning task 
		int noStateVars = r.getMyInteger(noInputs, DEF_STATEVARS);
		int noActions = r.getMyInteger(noOutputs, DEF_NOACTIONS);

		// configuration of sampling for state variables (float->finite no. of states) 
		double sampleMn = r.getMyDouble(sampleMin, DEF_MIN);
		double sampleMx = r.getMyDouble(sampleMax, DEF_MAX);
		int sampleC = r.getMyInteger(sampleCount, DEF_COUNT);

		this.myLog(me+"creating data structures");
		
		// build variable set (each variable has own encoder)
		BasicVariableEncoder enc = new BasicVariableEncoder(sampleMn, sampleMx, sampleC);
		BasicStateVariable[] vars = new BasicStateVariable[noStateVars];
		for(int i=0; i<vars.length; i++){
			vars[i] = new BasicStateVariable(statePrefix+i,enc);
		}
		states = new BasicFinalStateSet(vars);
		
		// build action set
		String[] names = new String[noActions];
		for(int i=0; i<noActions; i++)
			names[i] = actionPrefix+i;
		actions = new BasicFinalActionSet(names);
		actionEncoder = new OneOfNEncoder(actions);
		
		// build learning algorithm configuration
		NStepQLambdaConfImpl config = new NStepQLambdaConfImpl(len, lambda);
		config.setAlpha(alpha);
		config.setGamma(gamma);
		
		// configure the ASM
		BasicConfig asmConf = new BasicConfig();
		asm = new EpsilonGreedyDouble(actions, asmConf);
		asm.getConfig().setEpsilon(epsilon);
		asm.getConfig().setExplorationEnabled(true);
		
		// build learning algorithm
		learning = new FinalModelNStepQLambda(states, actions.getNumOfActions(), config);
		
	}


	@SuppressWarnings("unchecked")
	private void buildAlphaSubscriber(ConnectedNode connectedNode){
		// subscribe to given topic
		Subscriber<std_msgs.Float32MultiArray> alphaSub = 
				connectedNode.newSubscriber(name+s+alphaConfig, std_msgs.Float32MultiArray._TYPE);

		alphaSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			// print messages to console
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Received message has unexpected length of"+data.length+"!");
				else{
					logParamChange("RECEIVED chage of value Alpha",alpha, data[0]);
					alpha = data[0];
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
