package org.hanns.rl.discrete.ros.asm;

import java.util.LinkedList;

import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethod;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.ImportanceEpsGreedyDouble;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.actions.impl.OneOfNEncoder;
import org.hanns.rl.discrete.learningAlgorithm.lambda.impl.AbstractFinalModelNStepLambda;
import org.hanns.rl.discrete.learningAlgorithm.lambda.impl.NStepQLambdaConfImpl;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.FinalModelQLambda;
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

/**
 * Abstract ROS Node implementing the Action Selection Method (ASM). The ASM typically selects one action based on its utilities.
 * The ASM node typically sums utility values for particular actions from more sources. The computation of this node is as follows:  
 * <ul> 
 * <li>Utilities of actions are received as vector of real-valued scalars (each scalar represents one action utility).</li>
 * <li>The result of ASM computation is represented as transformation of this vector to another one.</li>
 * <li>Typically, the result of transformation will be the 1ofN code, representing one selected action with the scalar value of 1.</ul>
 * <li>The transformation is computed at each change of input values</ul>
 * </ul> 
 * 
 * @author Jaroslav Vitku
 *
 */
public abstract class AbstractASM extends AbstractConfigurableHannsNode{

	public static final String name = "AbstractASM";
	
	// enable randomization from the Nengoros simulator? (override the hardreset(true) to false?)
	public static final String randomizeConf = "randomize";
	public static final boolean DEF_RANDOMIZE = false;
	protected boolean randomizeAllowed;
	
	protected ActionSelectionMethod<Double> asm;// action selection methods

	protected OneOfNEncoder actionEncoder;		// encode actions to ROS
	protected BasicFinalActionSet actions;		// set of agents actions

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
		
		/*
		 * //TODO: implement some file logging
		 *  
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
		*/
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
		paramList.addParam(noOutputsConf, ""+DEF_NOOUTPUTS,"Number of actions available to the agent (1ofN coded)");

		paramList.addParam(logToFileConf, ""+DEF_LTF, "Enables logging into file");
		paramList.addParam(logPeriodConf, ""+DEF_LOGPERIOD, "How often to log?");
		paramList.addParam(randomizeConf, ""+DEF_RANDOMIZE, "Should allow RANDOMIZED reset from Nengo?");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void parseParameters(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		logToFile = r.getMyBoolean(logToFileConf, DEF_LTF);
		logPeriod = r.getMyInteger(logPeriodConf, DEF_LOGPERIOD);

		System.out.println(me+"parsing parameters");

		// dimensionality of the RL task 
		int noActions = r.getMyInteger(noOutputsConf, DEF_NOOUTPUTS);

		randomizeAllowed = r.getMyBoolean(randomizeConf, DEF_RANDOMIZE);

		System.out.println(me+"Creating data structures.");

		/**
		 * Build the action set & action encoder
		 */
		String[] names = new String[noActions];
		for(int i=0; i<noActions; i++)
			names[i] = actionPrefix+i;
		actions = new BasicFinalActionSet(names);
		actionEncoder = new OneOfNEncoder(actions);

		// TODO check this
		initializeASM(/*epsilon*/);
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
		this.buildASMSumbscribers(connectedNode);
	}

	/**
	 * Different AMSs can be used here, here subscribe for importance & epsilon
	 * 
	 * @param connectedNode ROS connectedNode 
	 */

	protected abstract void buildASMSumbscribers(ConnectedNode connectedNode);
	

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


