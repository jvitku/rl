package org.hanns.rl.discrete.ros.testnodes;


import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.hanns.rl.common.exceptions.DecoderException;
import org.hanns.rl.discrete.actions.ActionSetInt;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.actions.impl.OneOfNEncoder;
import org.hanns.rl.discrete.ros.sarsa.QLambda;
import org.hanns.rl.discrete.ros.testnodes.worlds.GridWorld;
import org.hanns.rl.discrete.states.impl.BasicVariableEncoder;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.network.node.AbstractConfigurableHannsNode;
import ctu.nengoros.network.node.infrastructure.rosparam.impl.PrivateRosparam;
import ctu.nengoros.network.node.infrastructure.rosparam.manager.ParamList;
import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;
import ctu.nengoros.util.SL;

/**
 * Provides very similar map to the one from src/test/java (used for testing) in for of a ROS node
 * compatible with RL ROS nodes. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class GridWorldNode extends AbstractConfigurableHannsNode{

	public static final String name = "GridWorldNode";
	public final String me = "["+name+"] ";

	protected PrivateRosparam r;

	public static final String shouldLog = "shouldLog";
	public static final boolean DEF_LOG = true;

	protected Log log;
	protected Publisher<std_msgs.Float32MultiArray> statePublisher;
	//private Publisher<std_msgs.Float32MultiArray> actionSubscriber;

	protected float[][] map;		// map of rewards
	protected int sizex, sizey;	// default dimensions of the map
	protected int logPeriod;		// how often to log
	protected int[] state;		// current state

	protected final int noActions = 4;	// 4 actions -> {<,>,^,v}
	//private final int stateLen = 2;		// 2 state variables -> x,y (published as raw floats from [0,1])
	protected float mapReward = 15;	// how much reward agent receives?

	public static final int DEF_SIZEX =10, DEF_SIZEY=10;
	public static final String sizexConf = "sizex"; // only one size supported so far
	public static final String sizeyConf = "sizey";

	public static final int DEF_LOGPERIOD = 100;			// how often to log, each 10 sim steps? 
	public static final String logPeriodConf = "logPeriod";


	protected BasicVariableEncoder stateEncoder;
	protected double rangeFrom = 0, rangeTo = 1;
	protected OneOfNEncoder actionEncoder;
	protected final ActionSetInt actionSet = new BasicFinalActionSet(new String[]{"<",">","^","v"}); 

	// whether some message from an agent received in the past 1000ms
	protected boolean dataExchanged = false;
	protected int step;
	protected volatile boolean simPaused = false;

	protected ParamList paramList;			// parameter storage
	
	protected LinkedList<Observer> observers;	

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();
		log.info(me+"started, parsing parameters. \n\nInfo: \n-This is simple 2D grid world."
				+ "\n-An agent has four actions: {<,>,^,v}."
				+ "\n-Each tale defines vlaue of reinforcement (mostly zeros)"
				+ "\n-By stepping on a tale, the reinforcement (value of a tale) is received."
				+ "\n-This node is subscribed to agents actions, it responds with "
				+ "a reinforcement and a new state immediatelly after receiving the action."
				+ "\n-Response is composed as follows: [float reward, float varX, float varY]\n\n");

		this.parseParameters(connectedNode);
		this.printParams();
		log.info(me+"Parameters parsed, creating the map of size: "+sizex+"x"+sizey+"\n\n");

		this.registerROSCommunication(connectedNode);

		this.defineMap();
		this.initData();

		state = this.getStartingPosition();
		this.observers = new LinkedList<Observer>();	// TODO: note that observing is not supported so far!

		super.fullName = super.getFullName(connectedNode);
		log.info(me+"Node configured and ready to provide simulator services!");
		this.registerSimulatorCommunication(connectedNode);
		this.waitForConnections(connectedNode);
	}

	protected int[] getStartingPosition(){
		return new int[]{(int)sizex/2, (int)sizey/2};	// start roughly in the center
	}

	protected void defineMap(){
		// create map, place the reinforcements
		map = GridWorld.simpleRewardMap(sizex, sizey, null, mapReward);
		map[2][2] = mapReward;	// place reward on the map
	}

	protected void initData(){

		step = 0;

		// need to encode x values in one float
		stateEncoder = new BasicVariableEncoder(rangeFrom,rangeTo,sizex);	
		actionEncoder = new OneOfNEncoder(actionSet);
	}

	/**
	 * This method is used for waiting for receiving communication.
	 * The node publishes current state of the environment, if in the 
	 * last second no message with action received. Newly connected agents 
	 * will respond with their action to this message.  
	 * 
	 * @param connectedNode
	 */
	protected void waitForConnections(ConnectedNode connectedNode){
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			@Override
			protected void setup() {}
			@Override
			protected void loop() throws InterruptedException {
				Thread.sleep(1000);
				if(!dataExchanged){
					log.info(me+"No agent detected, publishing the current state"+SL.toStr(state));
					std_msgs.Float32MultiArray fl = statePublisher.newMessage();
					fl.setData(encodeStateRewardMessage(0,state)); // state vars. to float[] 
					statePublisher.publish(fl);
				}
				dataExchanged = false;
			}
		});
	}

	protected void registerROSCommunication(ConnectedNode connectedNode){

		/**
		 * State publisher - connect to the input-data topic of (.e.g.) QLambda
		 */
		statePublisher =connectedNode.newPublisher(QLambda.topicDataIn, std_msgs.Float32MultiArray._TYPE);

		/**
		 * Action subscriber = subscribe to agents actions, process agents requests for simulating one step
		 */
		Subscriber<std_msgs.Float32MultiArray> epsilonSub = 
				connectedNode.newSubscriber(QLambda.topicDataOut, std_msgs.Float32MultiArray._TYPE);

		epsilonSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != noActions){
					log.error(me+"Received action description has" +
							"unexpected length of"+data.length+"! Expected number "
							+ "of actions (coding 1ofN) is "+noActions);
				}else{
					// try to decode action, if success, make simulation step and response with new data
					try {
						dataExchanged = true;
						int action = actionEncoder.decode(data);

						if((step)%logPeriod==0)
							log.info(me+"STEP: "+step+" Received agents action, this one: "+SL.toStr(data)
									+" the action no "+action);

						int[] newState = executeMapAction(action);
						float reward = map[newState[0]][newState[1]];

						std_msgs.Float32MultiArray fl = statePublisher.newMessage();
						fl.setData(encodeStateRewardMessage(reward,newState));								
						statePublisher.publish(fl);	// send a response with reinforcement and new state 
						state = newState.clone();

						if((step++)%logPeriod==0){
							System.out.println(me+"Responding with this state "+SL.toStr(newState)+
									" .. that is "+SL.toStr(encodeStateRewardMessage(reward,newState)));
							visMap();
						}

					} catch (DecoderException e) {
						log.error(me+"Unable to decode agents action, ignoring this message!");
						e.printStackTrace(); 
					}
				}
			}
		});
	}

	protected void visMap(){
		System.out.println(GridWorld.vis(map));
	}

	protected int[] executeMapAction(int action){
		int[] newState = GridWorld.makeStep(sizex, sizey, action, state);
		return newState;
	}

	/**
	 * Get the current simulation step.
	 * @return current simulation step
	 */
	public int getStep(){ return this.step; }

	/**
	 * THe simulation can be paused, during the paused simulation, the mapNode (this)
	 * is not responding with new state(reward) to a client (RL node). The simulation 
	 * can be resumed by the method {@link #setSimPaused(boolean)}.
	 * @return true if the simulation is not running
	 */
	public boolean getSimPaused(){ return this.simPaused; }

	/**
	 * Pause/resume the simulation
	 * @param paused true if the simulation should be paused
	 */
	public void setSimPaused(boolean paused){ this.simPaused = paused; }


	protected void parseParameters(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		paramList = new ParamList();


		// parse size of the map 
		sizex = r.getMyInteger(sizexConf, DEF_SIZEX);
		//sizey = r.getMyInteger(sizeyConf, DEF_SIZEY); 
		sizey = sizex; // TODO allow different sampling periods in the RL algorithm

		paramList.addParam(sizexConf, ""+DEF_SIZEX, "Currently, only square maps are supported, "
				+ "therefore this defines the size of map");

		paramList.addParam(logPeriodConf, ""+DEF_LOGPERIOD, "How often to log data to console?");
		logPeriod = r.getMyInteger(logPeriodConf, DEF_LOGPERIOD);
	}

	/**
	 * Get description of the environment state (agents position) and current
	 * reward. Encode this it into array of raw float values.
	 * The first value is the value of reinforcement, the rest values
	 * are encoded integer values of environment variables. 
	 * @param vars list of state variables
	 * @return vector of float values from the range between 0 and 1
	 */
	protected float[] encodeStateRewardMessage(float reward, int[] vars){
		float[] f = new float[vars.length+1];
		f[0] = reward;
		for(int i=1; i<=vars.length; i++){
			f[i] = stateEncoder.encode(vars[i-1]); 
		}
		return f;
	}

	protected void printParams(){
		String intro = "---------------------- Available parameters are: ";
		String outro = "------------------------------------------------";
		System.out.println(intro+"\n"+paramList.listParams()+"\n"+outro);
	}

	@Override
	public float getProsperity() { return 1; } // TODO, service provides should not have prosperity?

	@Override
	public String listParams() { return this.paramList.listParams(); }

	@Override
	protected void buildConfigSubscribers(ConnectedNode arg0) {
		// made before HannsNode
	}

	@Override
	protected void buildDataIO(ConnectedNode arg0) {
		// made before HannsNode
	}

	@Override
	public ProsperityObserver getProsperityObserver() {
		// no prosperity observer here
		return null;
	}

	@Override
	public boolean isStarted() {
		return (paramList!=null && log!=null && actionEncoder!=null && stateEncoder!=null);
	}

	@Override
	public void publishProsperity() { }

	@Override
	protected void registerParameters() {
		// made before HannsNode
	}

	@Override
	public String getFullName() { return this.fullName; }

	/**
	 * Pause the simulation, place the agent to the starting position, 
	 * set simulation step to 0 and resume the simulation
	 */
	@Override
	public void hardReset(boolean arg0) {
		this.simPaused = true;
		this.step = 0;
		this.state = this.getStartingPosition();
		this.simPaused = false;
	}

	@Override
	public void softReset(boolean arg0) {

	}

	@Override
	public LinkedList<Observer> getObservers() { return observers; }

}
