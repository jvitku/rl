package org.hanns.rl.discrete.ros.asm;

import java.util.LinkedList;

import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethod;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.actions.impl.OneOfNEncoder;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.impl.FinalModelNStepQLambda;
import org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.impl.NStepQLambdaConfImpl;
import org.hanns.rl.discrete.observer.visualizaiton.qMatrix.FinalStateSpaceVisDouble;
import org.hanns.rl.discrete.states.impl.BasicFinalStateSet;
import org.hanns.rl.discrete.states.impl.BasicStateVariable;
import org.hanns.rl.discrete.states.impl.BasicVariableEncoder;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

import ctu.nengoros.network.node.AbstractConfigurableHannsNode;
import ctu.nengoros.network.node.infrastructure.rosparam.impl.PrivateRosparam;
import ctu.nengoros.network.node.infrastructure.rosparam.manager.ParamList;
import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;

/**
 * Implements common functionalities of ROS node which does the Action Selection Methods (ASM).  
 * 
 * @author Jaroslav Vitku
 *
 */
public abstract class AbstractASMNode extends AbstractConfigurableHannsNode{

	/**
	 * Importance based Epsilon-greedy ASM configuration
	 */
	// importance affect current value of epsilon: higher action importance, smaller eps.
	
	public static final String importanceConf = "importance";
	public static final String topicImportance = conf+importanceConf;
	public static final double DEF_IMPORTANCE = ImportanceBasedConfig.DEF_IMPORTANCE;
	
	// enable randomization from the Nengoros simulator? (override the hardreset(true) to false?)
	public static final String randomizeConf = "randomize";
	public static final boolean DEF_RANDOMIZE = false;
	protected boolean randomizeAllowed;
	

	protected ActionSelectionMethod<Double> asm;// action selection methods
	
	// TODO synchronization
	protected int prevAction;					// index of the last action executed
	protected int step = 0;

	
	// TODO ??
	protected OneOfNEncoder actionEncoder;		// encode actions to ROS
	protected BasicFinalActionSet actions;		// set of agents actions
	
	protected ProsperityObserver o;						// observes the prosperity of node
	protected LinkedList<Observer> observers;	// logging, visualization & observing data

	

	@Override
	public void onStart(ConnectedNode connectedNode) {
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
		// initialize the visualizer
		FinalStateSpaceVisDouble visualization = new FinalStateSpaceVisDouble(
				states.getDimensionsSizes(), actions.getNumOfActions(), q);

		visualization.setVisPeriod(this.logPeriod);
		visualization.setTypeVisualization(2);
		visualization.setActionRemapping(new String[]{"<",">","^","v"});

		observers.add(visualization);
		*/
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
	
	@Override
	public String listParams() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getFullName() { return this.fullName; }

	@Override
	public boolean isStarted() {
		if(log==null)
			return false;
		if(prospPublisher==null)
			return false;
		if(dataPublisher==null)
			return false;
		if(asm==null)
			return false;
		if(observers==null)
			return false;
		return true;
	}

	@Override
	public abstract void hardReset(boolean randomize);

	@Override
	public abstract void softReset(boolean randomize);

	@Override
	public LinkedList<Observer> getObservers() { return observers; }

	@Override
	public abstract float getProsperity();

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
	public GraphName getDefaultNodeName() { return GraphName.of(name); }


	@Override
	protected void buildConfigSubscribers(ConnectedNode connectedNode) {
		//dataPublisher =connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE);
		//TODO
	}


	@Override
	protected void buildDataIO(ConnectedNode arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public ProsperityObserver getProsperityObserver() { return o; }
	
	protected abstract void initializeASM();

	/**
	 * This method is called by the dataSubscriber when a new ROS 
	 * message with state and reward description arrives.
	 */
	protected abstract void onNewDataReceived(float[] data);


	@Override
	protected void parseParameters(ConnectedNode connectedNode) {
		r = new PrivateRosparam(connectedNode);
		logToFile = r.getMyBoolean(logToFileConf, DEF_LTF);
		logPeriod = r.getMyInteger(logPeriodConf, DEF_LOGPERIOD);

		System.out.println(me+"parsing parameters");

		// dimensionality of the RL task 
		int noActions = r.getMyInteger(noOutputsConf, DEF_NOOUTPUTS);

		randomizeAllowed = r.getMyBoolean(randomizeConf, DEF_RANDOMIZE);
		System.out.println(me+"Creating data structures.");

		// TODO 
		/**
		 * Build the action set & action encoder
		 */
		String[] names = new String[noActions];
		for(int i=0; i<noActions; i++)
			names[i] = actionPrefix+i;
		actions = new BasicFinalActionSet(names);
		actionEncoder = new OneOfNEncoder(actions);
		
		initializeASM(/*epsilon*/);
	}


	@Override
	protected void registerParameters() {
		paramList = new ParamList();
		paramList.addParam(noInputsConf, ""+DEF_NOINPUTS,"Number of state variables");

		paramList.addParam(logToFileConf, ""+DEF_LTF, "Enables logging into file");
		paramList.addParam(logPeriodConf, ""+DEF_LOGPERIOD, "How often to log?");
		paramList.addParam(randomizeConf, ""+DEF_RANDOMIZE, "Should allow RANDOMIZED reset from Nengo?");
	}
}
