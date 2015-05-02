package org.hanns.rl.discrete.ros.asm;

import java.util.LinkedList;

import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethod;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.actions.impl.OneOfNEncoder;
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
		//this.registerObservers(); // TODO

		System.out.println(me+"initializing ROS Node IO");

		this.registerSimulatorCommunication(connectedNode); // listen to hard/soft resets etc..
		this.buildProsperityPublisher(connectedNode);		// output for Prosperity value(s)
		this.buildConfigSubscribers(connectedNode);
		this.buildDataIO(connectedNode);

		super.fullName = super.getFullName(connectedNode);
		System.out.println(me+"Node configured and ready now! Running: "+this.isStarted());

	}

	/**
	 * Adds arbitrary observers/visualizators to the node/algorithms
	 */
	protected void registerObservers(){
		observers = new LinkedList<Observer>();

		this.registerProsperityObserver();

		/*
		 * //TODO: implement some file logging in the observer.asm.stats/visualization
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
	 * Instantiate the Observer {@link #o} to the resider one. 
	 */
	protected abstract void registerProsperityObserver();

	@Override
	protected void registerParameters(){
		paramList = new ParamList();
		paramList.addParam(noInputsConf, ""+DEF_NOINPUTS,"Number of acitons to select from");

		paramList.addParam(logToFileConf, ""+DEF_LTF, "Enables logging into file");
		paramList.addParam(logPeriodConf, ""+DEF_LOGPERIOD, "How often to log?");
	}

	@Override
	protected void parseParameters(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		logToFile = r.getMyBoolean(logToFileConf, DEF_LTF);
		logPeriod = r.getMyInteger(logPeriodConf, DEF_LOGPERIOD);

		System.out.println(me+"parsing parameters");

		// noActions 
		int noActions = r.getMyInteger(noInputsConf, DEF_NOINPUTS);

		System.out.println(me+"Creating data structures.");

		/**
		 * Build the action set & action encoder
		 */
		String[] names = new String[noActions];
		for(int i=0; i<noActions; i++)
			names[i] = actionPrefix+i;
		actions = new BasicFinalActionSet(names);
		actionEncoder = new OneOfNEncoder(actions);

		initializeASM();
	}

	/**
	 * Create data structures for the ASM, such as ASM and it's configuration.
	 */
	protected abstract void initializeASM();

	/**
	 * This method is called by the dataSubscriber when a new ROS 
	 * message with list of utility values is received
	 */
	protected abstract float[] selectActionAndEncode(float[] data);

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
				if(data.length != actions.getNumOfActions())
					log.error(me+":"+topicDataIn+": Received array of actions of" +
							"unexpected length of"+data.length+"! Expected was: "+
							(actions.getNumOfActions()));
				else{
					// here, the state description is decoded and one SARSA step executed
					if(step % logPeriod==0)
						System.out.println(me+"<-"+topicDataIn+" Received new list of actions" +
								SL.toStr(data));

					// implement this in order to implement the ASM
					sendAction(selectActionAndEncode(data));
				}
			}
		});
	}
	
	/**
	 * Vector of real values will be published. Typically 1ofN code, where 1 means
	 * the selected action. 
	 * 
	 * @param vector of float values to be published
	 */
	public void sendAction(float[] data){
		std_msgs.Float32MultiArray fl = dataPublisher.newMessage();	
		fl.setData(data);
		dataPublisher.publish(fl);
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
	 * Different AMSs can be used here, here subscribe e.g. for importance & epsilon
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

		// TODO call this method each step somewhere

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
	public float getProsperity() { return o.getProsperity(); }

	@Override
	public String listParams() { return this.paramList.listParams(); }
	
	@Override
	public void hardReset(boolean randomize) {
			
		//System.out.println(me+"hardReset called, discarding all data");
		for(int i=0; i<observers.size(); i++){
			observers.get(i).hardReset(randomize);
		}
		o.hardReset(randomize);
	}

	@Override
	public void softReset(boolean randomize) {
		
		//System.out.println(me+"softReset called, returning to the initial state.");
		for(int i=0; i<observers.size(); i++){
			observers.get(i).softReset(randomize);
		}
		o.softReset(randomize);
	}

	@Override
	public ProsperityObserver getProsperityObserver() { return o; }

	@Override
	public boolean isStarted() {
		if(log==null)
			return false;
		/*
		if(prospPublisher==null)
			return false;
		 *///TODO
		if(dataPublisher==null)
			return false;
		if(asm==null)
			return false;
		/*
		if(observers==null)
			return false;
		 *///TODO
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


