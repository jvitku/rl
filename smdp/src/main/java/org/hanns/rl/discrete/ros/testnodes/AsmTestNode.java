package org.hanns.rl.discrete.ros.testnodes;

import java.util.LinkedList;
import java.util.Random;

import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethod;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.actions.impl.OneOfNEncoder;
import org.hanns.rl.discrete.states.impl.BasicFinalStateSet;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.network.node.AbstractConfigurableHannsNode;
import ctu.nengoros.network.node.infrastructure.rosparam.impl.PrivateRosparam;
import ctu.nengoros.network.node.infrastructure.rosparam.manager.ParamList;
import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;

/**
 * Should test all the 3 ASM nodes. TODO implement this.
 * 
 * @author Jaroslav Vitku
 *
 */
public class AsmTestNode extends AbstractConfigurableHannsNode{

	public static final String name = "AsmTestNode";

	protected ActionSelectionMethod<Double> asm;// action selection methods

	protected OneOfNEncoder actionEncoder;		// encode actions to ROS
	protected BasicFinalActionSet actions;		// set of agents actions

	protected BasicFinalStateSet states;		// state variables (each has encoder)

	protected int step = 0;

	protected ProsperityObserver o;						// observes the prosperity of node
	protected LinkedList<Observer> observers;	// logging, visualization & observing data

	protected final String randomizationConf = "Randomize";
	protected final boolean DEF_RANDOMIZATION = false;
	public boolean randomizationAllowed = DEF_RANDOMIZATION;
	
	protected int noActions;
	public int noCorrect, noIncorrect;
	private int bestPrevActionInd;
	public boolean dataErrorFound = false; 
	
	private boolean simulationPaused = false;
	
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
		this.buildDataIO(connectedNode);

		super.fullName = super.getFullName(connectedNode);
		
		noCorrect=0;
		noIncorrect=0;
		this.bestPrevActionInd = 0;
		
		System.out.println(me+"Node configured and ready now!");
	}

	/**
	 * Adds arbitrary observers/visualizators to the node/algorithms
	 */
	protected void registerObservers(){
		observers = new LinkedList<Observer>();
		this.registerProsperityObserver();
	}
	
	/**
	 * Instatntiate the Observer {@link #o} to the resider one. 
	 */
	protected void registerProsperityObserver(){}


	@Override
	protected void registerParameters(){
		paramList = new ParamList();
		paramList.addParam(noInputsConf, ""+DEF_NOINPUTS,"Number of state variables");
		paramList.addParam(randomizationConf, ""+DEF_RANDOMIZATION, "Does the tested node implement a randomization?");
	}

	@Override
	protected void parseParameters(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		System.out.println(me+"parsing parameters");

		// dimensionality of the RL task 
		noActions = r.getMyInteger(noInputsConf, DEF_NOINPUTS);
		
		randomizationAllowed = r.getMyBoolean(randomizationConf, DEF_RANDOMIZATION);

		/**
		 * Build the action set & action encoder
		 */
		String[] names = new String[noActions];
		for(int i=0; i<noActions; i++)
			names[i] = actionPrefix+i;
		actions = new BasicFinalActionSet(names);
		actionEncoder = new OneOfNEncoder(actions);

	}

	/**
	 * Check the performance of the node
	 */
	protected void onNewDataReceived(float[] data){
	
		// check the response from the ASM
		int selected = this.getSelectedActionInd(data);
		if(selected != bestPrevActionInd){
			noIncorrect++;
		}else{
			noCorrect++;
		}
		// send new test data
		this.generateUtilsAndSend();
		
	}

	private int getSelectedActionInd(float[] data){
		int selected = -1;
		boolean found = false;
		
		for(int i=0; i<data.length; i++){
			if(data[i]==1){
				if(found){
					this.dataErrorFound = true;
					return 0;
				}else{
					found = true;
					selected = i;
				}
			}else if(data[i]!=0){
				this.dataErrorFound = true;
				return 0;
			}
		}
		if(!found){
			this.dataErrorFound = true;
			return -0;
		}
		return selected;
	}
	
	private void generateUtilsAndSend(){
		
		float[] newData = new float[this.noActions];
		int bestInd = 0;
		Random r = new Random();
		
		for(int i=0; i<this.noActions; i++){
			newData[i] = r.nextFloat();
			if(newData[i]>newData[bestInd]){
				bestInd = i;
			}
		}
		this.bestPrevActionInd = bestInd;	// remember for the response
		
		std_msgs.Float32MultiArray fl = dataPublisher.newMessage();	
		fl.setData(newData);
		
		dataPublisher.publish(fl);
	}
	
	/**
	 * Register the {@link #actionPublisher} for publishing actions 
	 * and the state subscriber for receiving state+reward data.
	 * 
	 * @param connectedNode
	 */
	protected void buildDataIO(ConnectedNode connectedNode){
		// publish vector of action utilities 
		dataPublisher =connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE);

		Subscriber<std_msgs.Float32MultiArray> dataSub = 
				connectedNode.newSubscriber(topicDataIn, std_msgs.Float32MultiArray._TYPE);

		// check the 1ofN response, either randomized or returning the index of max action
		dataSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				
				if(simulationPaused){
					return;
				}
				
				float[] data = message.getData();
				if(data.length != actions.getNumOfActions())
					log.error(me+":"+topicDataIn+": Received vector of actions of " +
							"unexpected length of"+data.length+"! Expected: "+
							(actions.getNumOfActions()));
				else{
					// implement this
					onNewDataReceived(data);
				}
				step++;
			}
		});
	}

	/**
	 * Register subscribers for the RL configuration (alpha & gamma)
	 * @param connectedNode
	 */
	@Override
	protected void buildConfigSubscribers(ConnectedNode connectedNode){}


	/**
	 * If the prosperity observer has no childs, publish its value. 
	 * If the prosperity observer has childs, publish its value on the first
	 * position and values of its childs in the vector.
	 */
	@Override
	public void publishProsperity(){
	}

	@Override
	public ProsperityObserver getProsperityObserver() { return o; }

	@Override
	public boolean isStarted() {
		if(log==null)
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
	public String getFullName() { return this.fullName; }

	@Override
	public LinkedList<Observer> getObservers() { return observers; }
	
	
	private boolean lg = false;
	public void logg(String what) {
		if(lg)
			System.out.println(" ------- "+what);		
	}

	@Override
	public String listParams() {
		return null;
	}

	@Override
	public void hardReset(boolean arg0) {}

	@Override
	public void softReset(boolean arg0) {}

	@Override
	public float getProsperity() { return 0; }
	
	public int getStep(){
		return this.step;
	}
	
	public void pauseSimulation(boolean pause){
		this.simulationPaused = pause;
	}
}


