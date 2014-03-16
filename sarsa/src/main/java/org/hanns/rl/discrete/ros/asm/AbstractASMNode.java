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
 * <p>Implements common functionalities of ROS node which does the Action Selection Methods (ASM).</p>
 * 
 * <p>ASM node takes N inputs, each corresponding to one possible action. Value on each input
 * represents current utility of an action (higher value, higher reward expected, negative values, 
 * punishment). The node has N outputs, where the code 1ofN is used to represent the action that is 
 * selected by the ASM.</p>   
 * 
 * @author Jaroslav Vitku
 *
 */
public abstract class AbstractASMNode extends AbstractConfigurableHannsNode{


	protected int noActions;	// N actions to select from

	// enable randomization from the Nengoros simulator? (override the hardreset(true) to false?)
	public static final String randomizeConf = "randomize";
	public static final boolean DEF_RANDOMIZE = false;
	protected boolean randomizeAllowed;
	
	protected ActionSelectionMethod<Float> asm;	// action selection methods
	
	protected int prevAction = 0;
	protected int step = 0;

	protected OneOfNEncoder actionEncoder;		// encode actions to ROS
	protected BasicFinalActionSet actions;		// set of agents actions
	
	protected ProsperityObserver o;				// observes the prosperity of node
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
	 * Adds arbitrary observers/visualizers to the node/algorithms
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
	 * Execute action selected by the ASM and publish over the ROS network
	 * 
	 * @param action index of selected action, this action is encoded and sent
	 */
	protected void executeAction(int action){
		if((step++) % logPeriod==0) 
			log.info(me+"Step: "+step+"-> the following action is selected "
					+SL.toStr(actionEncoder.encode(action)));

		std_msgs.Float32MultiArray fl = dataPublisher.newMessage();	
		fl.setData(actionEncoder.encode(action));								
		dataPublisher.publish(fl);

		prevAction = action;
		this.publishProsperity();
	}

	/**
	 * Different AMSs can be used here, here subscribe for importance; epsilon etc..
	 * 
	 * @param connectedNode ROS connectedNode 
	 */
	@Override
	protected abstract void buildConfigSubscribers(ConnectedNode connectedNode);
	
	/**
	 * Instantiate the Observer {@link #o} to the resider one. 
	 */
	protected void registerProsperityObserver() {
		// TODO implement some prosperity observers
	}

	@Override
	public float getProsperity() {
		// TODO implement some prosperity observer
		return 0;
	}
	

	@Override
	public String listParams() { return this.paramList.listParams(); }


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
	public LinkedList<Observer> getObservers() { return observers; }

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
	protected void buildDataIO(ConnectedNode connectedNode) {
		dataPublisher =connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE);

		Subscriber<std_msgs.Float32MultiArray> dataSub = 
				connectedNode.newSubscriber(topicDataIn, std_msgs.Float32MultiArray._TYPE);

		dataSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != noActions)
					log.error(me+":"+topicDataIn+": Received array of action utilities has" +
							"unexpected length of"+data.length+"! Expected: "+noActions);
				else{
					// here, the state description is decoded and one SARSA step executed
					if(step % logPeriod==0)
						System.out.println(me+"<-"+topicDataIn+" Received new array of action" +
								" utilities, these are: "+SL.toStr(data));

					// implement this
					onNewDataReceived(data);
				}
			}
		});
	}

	@Override
	public void hardReset(boolean randomize) {
		if(!this.randomizeAllowed)
			randomize = false;
		asm.hardReset(randomize);
	}

	@Override
	public void softReset(boolean randomize) {
		if(!this.randomizeAllowed)
			randomize = false;
		asm.softReset(randomize);
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

		randomizeAllowed = r.getMyBoolean(randomizeConf, DEF_RANDOMIZE);
		System.out.println(me+"Creating data structures.");
		

		// parse the number of actions to select from 
		noActions = r.getMyInteger(noInputsConf, DEF_NOINPUTS);
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
		paramList.addParam(noInputsConf, ""+DEF_NOINPUTS,"N-Number of actions to select from (output: 1ofN code)");
		paramList.addParam(logToFileConf, ""+DEF_LTF, "Enables logging into file");
		paramList.addParam(logPeriodConf, ""+DEF_LOGPERIOD, "How often to log?");
		paramList.addParam(randomizeConf, ""+DEF_RANDOMIZE, "Should allow RANDOMIZED reset from Nengo?");
	}
}
