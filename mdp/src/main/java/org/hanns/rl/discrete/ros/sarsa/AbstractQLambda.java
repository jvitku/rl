package org.hanns.rl.discrete.ros.sarsa;


import java.util.LinkedList;

import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethod;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.ImportanceEpsGreedyDouble;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.actions.impl.OneOfNEncoder;
import org.hanns.rl.discrete.learningAlgorithm.lambda.impl.AbstractFinalModelNStepLambda;
import org.hanns.rl.discrete.learningAlgorithm.lambda.impl.NStepQLambdaConfImpl;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.impl.FinalModelQLambda;
import org.hanns.rl.discrete.observer.qMatrix.visualizaiton.FinalStateSpaceVisDouble;
import org.hanns.rl.discrete.states.impl.BasicFinalStateSet;
import org.hanns.rl.discrete.states.impl.BasicStateVariable;
import org.hanns.rl.discrete.states.impl.BasicVariableEncoder;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.network.node.AbstractConfigurableHannsNode;
import ctu.nengoros.network.node.infrastructure.rosparam.impl.PrivateRosparam;
import ctu.nengoros.network.node.infrastructure.rosparam.manager.ParamList;
import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;
import ctu.nengoros.util.SL;

public abstract class AbstractQLambda extends AbstractConfigurableHannsNode{

	public static final String name = "QLambda";

	/**
	 * RL parameters
	 */
	// learning rate
	public static final String alphaConf = "alpha";
	public static final String topicAlpha= conf+alphaConf;
	public static final double DEF_ALPHA = 0.5;
	// Decay factor
	public static final String gammaConf = "gamma";
	public static final String topicGamma = conf+gammaConf;
	public static final double DEF_GAMMA = 0.3;
	// Trace decay factor
	public static final String lambdaConf = "lambda";
	public static final String topicLambda = conf+lambdaConf;
	// Length of eligibility trace
	public static final double DEF_LAMBDA = 0.04;
	public static final String traceLenConf = "traceLenConf";
	public static final int DEF_TRACELEN = 10;

	/**
	 * Importance based Epsilon-greedy ASM configuration
	 */
	// probability of choosing action randomly
	//public static final String epsilonConf="epsilon"; // TOOD change minEpsilon
	//public static final String topicEpsilon = conf+epsilonConf;
	//public static final double DEF_EPSILON=0.6;
	// importance affect current value of epsilon: higher action importance, smaller eps.
	public static final String importanceConf = "importance";
	public static final String topicImportance = conf+importanceConf;
	public static final double DEF_IMPORTANCE = ImportanceBasedConfig.DEF_IMPORTANCE;
	
	// enable randomization from the Nengoros simulator? (override the hardreset(true) to false?)
	public static final String randomizeConf = "randomize";
	public static final boolean DEF_RANDOMIZE = false;
	protected boolean randomizeAllowed;

	/**
	 * Default sampling parameters, TODO: customize each variable sampling 
	 * independently
	 * 
	 * Sampling is from the interval [{@link #sampleMinConf}, 
	 * {@link #sampleMaxConf}] with 
	 * {@link #sampleCountConf} of samples.
	 */
	public static final String sampleMinConf="sampleMin",
			sampleMaxConf="sampleMax", sampleCountConf="sampleCount";

	public static final double DEF_MIN=0, DEF_MAX=1;
	public static final int DEF_COUNT=5;


	/**
	 * RL instances
	 */
	public AbstractFinalModelNStepLambda rl;			// RL algorithm
	protected FinalQMatrix<Double> q;			// Q(s,a) matrix used by the RL
	protected ActionSelectionMethod<Double> asm;// action selection methods

	protected OneOfNEncoder actionEncoder;		// encode actions to ROS
	protected BasicFinalActionSet actions;		// set of agents actions

	protected BasicFinalStateSet states;		// state variables (each has encoder)

	protected int prevAction;					// index of the last action executed
	protected int step = 0;

	protected ProsperityObserver o;						// observes the prosperity of node
	protected LinkedList<Observer> observers;	// logging, visualization & observing data


	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();

		log.info(me+"started, parsing parameters");
		this.registerParameters();
		paramList.printParams();
		this.parseParameters(connectedNode);
		this.registerObservers();

		System.out.println(me+"initializing ROS Node IO");

		this.registerSimulatorCommunication(connectedNode);
		this.buildProsperityPublisher(connectedNode);
		this.buildConfigSubscribers(connectedNode);
		this.buildDataIO(connectedNode);

		super.fullName = super.getFullName(connectedNode);
		
		System.out.println(me+"Node configured and ready now!");
	}

	/**
	 * Adds arbitrary observers/visualizators to the node/algorithms
	 */
	protected void registerObservers(){
		observers = new LinkedList<Observer>();

		this.registerProsperityObserver();
		
		// initialize the visualizer
		FinalStateSpaceVisDouble visualization = new FinalStateSpaceVisDouble(
				states.getDimensionsSizes(), actions.getNumOfActions(), q);

		visualization.setVisPeriod(this.logPeriod);
		visualization.setTypeVisualization(2);
		visualization.setActionRemapping(new String[]{"<",">","^","v"});

		observers.add(visualization);
		// configure observers to log/visualize in as selected in the node
		for(int i=0; i<observers.size(); i++){
			System.out.println("willLogToFile "+this.logToFile+ " period: "+this.logPeriod+
					" "+observers.get(i).getName());
			observers.get(i).setShouldVis(this.logPeriod>=0);
			observers.get(i).setVisPeriod(this.logPeriod);
		}
	}
	
	/**
	 * Instatntiate the Observer {@link #o} to the resider one. 
	 */
	protected abstract void registerProsperityObserver();

	/**
	 * Execute action selected by the ASM and publish over the ROS network
	 * 
	 * @param action index of selected action, this action is encoded and sent
	 */
	protected void executeAction(int action){
		if((step++) % logPeriod==0) 
			log.info(me+"Step: "+step+"-> responding with the following action: "
					+SL.toStr(actionEncoder.encode(action)));

		// publish action selected by the ASM
		std_msgs.Float32MultiArray fl = dataPublisher.newMessage();	
		fl.setData(actionEncoder.encode(action));								
		dataPublisher.publish(fl);

		prevAction = action;

		this.publishProsperity();
	}

	@Override
	protected void registerParameters(){
		paramList = new ParamList();
		paramList.addParam(noInputsConf, ""+DEF_NOINPUTS,"Number of state variables");
		paramList.addParam(sampleCountConf, ""+DEF_COUNT, "Number of samples for variables, that is: number of values!");
		paramList.addParam(sampleMinConf, ""+DEF_MIN,"Min. value on the state input");
		paramList.addParam(sampleMaxConf, ""+DEF_MAX,"Max. value on the state input");
		paramList.addParam(noOutputsConf, ""+DEF_NOOUTPUTS,"Number of actions available to the agent (1ofN coded)");

		paramList.addParam(logToFileConf, ""+DEF_LTF, "Enables logging into file");
		paramList.addParam(logPeriodConf, ""+DEF_LOGPERIOD, "How often to log?");
		paramList.addParam(alphaConf, ""+DEF_ALPHA, "Learning rate");
		paramList.addParam(gammaConf, ""+DEF_GAMMA, "Decay rate");
		paramList.addParam(lambdaConf, ""+DEF_LAMBDA, "Trace decay rate");
		paramList.addParam(traceLenConf, ""+DEF_TRACELEN, "Length of eligibility trace");
		paramList.addParam(randomizeConf, ""+DEF_RANDOMIZE, "Should allow RANDOMIZED reset from Nengo?");
		//paramList.addParam(epsilonConf, ""+DEF_EPSILON,"Probability of randomizing selected action");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void parseParameters(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		logToFile = r.getMyBoolean(logToFileConf, DEF_LTF);
		logPeriod = r.getMyInteger(logPeriodConf, DEF_LOGPERIOD);

		System.out.println(me+"parsing parameters");

		// RL parameters (default alpha and gamma, but can be also modified online)
		double alpha = r.getMyDouble(alphaConf, DEF_ALPHA);
		double gamma = r.getMyDouble(gammaConf, DEF_GAMMA);
		double lambda = r.getMyDouble(lambdaConf, DEF_LAMBDA);
		int len = r.getMyInteger(traceLenConf, DEF_TRACELEN);
		//double epsilon = r.getMyDouble(epsilonConf, DEF_EPSILON);

		// dimensionality of the RL task 
		int noStateVars = r.getMyInteger(noInputsConf, DEF_NOINPUTS);
		int noActions = r.getMyInteger(noOutputsConf, DEF_NOOUTPUTS);

		// configuration of sampling for state variables (float->finite no. of states) 
		double sampleMn = r.getMyDouble(sampleMinConf, DEF_MIN);
		double sampleMx = r.getMyDouble(sampleMaxConf, DEF_MAX);
		int sampleC = r.getMyInteger(sampleCountConf, DEF_COUNT);
		randomizeAllowed = r.getMyBoolean(randomizeConf, DEF_RANDOMIZE);

		System.out.println(me+"Creating data structures.");

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
		 * Build the action set & action encoder
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

		initializeASM(/*epsilon*/);
		/**
		 *  build the RL algorithm and obtain its Q(s,a) matrix
		 */
		// TODO package structure changed, check this
		rl = new FinalModelQLambda(states, actions.getNumOfActions(), config);
		q = (FinalQMatrix<Double>)(rl.getMatrix());
	}

	protected void initializeASM(/*double epsilon*/){
		ImportanceBasedConfig asmConf = new ImportanceBasedConfig();
		asm = new ImportanceEpsGreedyDouble(actions, asmConf);
		asm.getConfig().setExplorationEnabled(true);

		// this forces the agent to use only greedy ASM when importance is 1 
		//((ImportanceEpsGreedyDouble)asm).getConfig().setMinEpsilon(0);
	}


	/**
	 * This method is called by the dataSubscriber when a new ROS 
	 * message with state and reward description arrives.
	 */
	protected abstract void onNewDataReceived(float[] data);

	/**
	 * Register the {@link #actionPublisher} for publishing actions 
	 * and the state subscriber for receiving state+reward data.
	 * 
	 * @param connectedNode
	 */
	protected void buildDataIO(ConnectedNode connectedNode){
		dataPublisher =connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE);

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
					if(step % logPeriod==0)
						System.out.println(me+"<-"+topicDataIn+" Received new reinforcement &" +
								" state description "+SL.toStr(data));

					// implement this
					onNewDataReceived(data);
				}
			}
		});
	}

	/**
	 * Register subscribers for the RL configuration (alpha & gamma)
	 * @param connectedNode
	 */
	@Override
	protected void buildConfigSubscribers(ConnectedNode connectedNode){

		this.buildRLConfigSubscribers(connectedNode);
		this.buildEligibilitySubscribers(connectedNode);
		this.buildASMSumbscribers(connectedNode);

	}

	protected void buildRLConfigSubscribers(ConnectedNode connectedNode){
		/**
		 * Alpha
		 */
		Subscriber<std_msgs.Float32MultiArray> alphaSub = 
				connectedNode.newSubscriber(topicAlpha, std_msgs.Float32MultiArray._TYPE);

		alphaSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Gamma config: Received message has " +
							"unexpected length of"+data.length+"!");
				else{
					logParamChange("RECEIVED chage of value ALPHA",
							rl.getConfig().getAlpha(), data[0]);
					rl.getConfig().setAlpha(data[0]);
				}
			}
		});

		/**
		 * Gamma
		 */
		Subscriber<std_msgs.Float32MultiArray> gammaSub = 
				connectedNode.newSubscriber(topicGamma, std_msgs.Float32MultiArray._TYPE);

		gammaSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Gamma config: Received message has" +
							" unexpected length of"+data.length+"!");
				else{
					logParamChange("RECEIVED chage of value GAMMA",
							rl.getConfig().getGamma(), data[0]);
					rl.getConfig().setGamma(data[0]);
				}
			}
		});	
	}


	/**
	 * Register configuration data-lines for the ASM (epsilon & importance).
	 * 
	 * @param connectedNode
	 */
	protected void buildEligibilitySubscribers(ConnectedNode connectedNode){
		/**
		 * Lambda
		 */
		Subscriber<std_msgs.Float32MultiArray> lambdaSub = 
				connectedNode.newSubscriber(topicLambda, std_msgs.Float32MultiArray._TYPE);

		lambdaSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Lambda config: Received message has" +
							" unexpected length of"+data.length+"!");
				else{
					logParamChange("RECEIVED chage of value LAMBDA",
							rl.getConfig().getLambda(), data[0]);
					rl.getConfig().setLambda(data[0]);
				}
			}
		});
	}

	/**
	 * Different AMSs can be used here, here subscribe for importance & epsilon
	 * 
	 * @param connectedNode ROS connectedNode 
	 */

	protected void buildASMSumbscribers(ConnectedNode connectedNode){
		/**
		 * Epsilon
		 * /
		Subscriber<std_msgs.Float32MultiArray> epsilonSub = 
				connectedNode.newSubscriber(topicEpsilon, std_msgs.Float32MultiArray._TYPE);

		epsilonSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Epsilon config: Received message has " +
							"unexpected length of"+data.length+"!");
				else{

					logParamChange("RECEIVED chage of value EPSILON",
							((EpsilonGreedyDouble)asm).getConfig().getEpsilon(),data[0]);
					((EpsilonGreedyDouble)asm).getConfig().setEpsilon(data[0]);
				}
			}
		});
		 */
		/**
		 * Importance parameter
		 */
		Subscriber<std_msgs.Float32MultiArray> importenceSub = 
				connectedNode.newSubscriber(topicImportance, std_msgs.Float32MultiArray._TYPE);

		importenceSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Importance input: Received message has " +
							"unexpected length of"+data.length+"!");
				else{
					logParamChange("RECEIVED chage of value IMPORTANCE",
							((ImportanceBasedConfig)asm.getConfig()).getImportance(), data[0]);

					((ImportanceBasedConfig)asm.getConfig()).setImportance(data[0]);
				}
			}
		});
	}

	/**
	 * If the prosperity observer has no childs, publish its value. 
	 * If the prosperity observer has childs, publish its value on the first
	 * position and values of its childs in the vector.
	 */
	@Override
	public void publishProsperity(){

		float[] data;
		std_msgs.Float32MultiArray fl = prospPublisher.newMessage();

		if(o.getChilds() == null){
			data = new float[]{o.getProsperity()};
		}else{
			ProsperityObserver[] childs = o.getChilds();	
			data = new float[childs.length+1];
			data[0] = o.getProsperity();

			for(int i=0; i<childs.length; i++){
				data[i+1] = childs[i].getProsperity();
			}
		}
		fl.setData(data);
		prospPublisher.publish(fl);
	}

	@Override
	public ProsperityObserver getProsperityObserver() { return o; }

	@Override
	public boolean isStarted() {
		if(log==null)
			return false;
		if(prospPublisher==null)
			return false;
		if(dataPublisher==null)
			return false;
		if(asm==null || q==null || rl==null)
			return false;
		if(observers==null)
			return false;
		return true;
	}
	
	@Override
	public String getFullName() { return this.fullName; }

	@Override
	public LinkedList<Observer> getObservers() { return observers; }
	
	
	private boolean lg = false;
	public void logg(String what) {
		if(lg)
			System.out.println(" ------- "+what);		
	}
}


