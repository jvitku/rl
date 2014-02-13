package org.hanns.rl.discrete.ros.testnodes;


import java.util.LinkedList;

import org.hanns.rl.common.exceptions.DecoderException;
import org.hanns.rl.discrete.ros.sarsa.QLambda;
import org.hanns.rl.discrete.ros.testnodes.worlds.GridWorld;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.util.SL;

/**
 * Provides very similar map to the one from src/test/java (used for testing) in for of a ROS node
 * compatible with RL ROS nodes. 
 * 
 * Note that this may not accept sizex parameter!
 * 
 * @author Jaroslav Vitku
 *
 */
public class TwoRewardGridWorldNode extends GridWorldNode{

	public static final String name = "TwoRewardGridWorldNode";
	
	//protected float[][] map;		// map of rewards
	public static final float DEF_REWARDVAL = 15;
	protected float rewardAVal = 11;	// this value produces (represents) rewardA
	protected float rewardBVal = 12;	// this value produces rewardB
	
	//protected int sizex, sizey;	// default dimensions of the map
	//protected int[] state;		// current state

	//protected final int noActions = 4;	// 4 actions -> {<,>,^,v}
	//private final int stateLen = 2;		// 2 state variables -> x,y (published as raw floats from [0,1])
	
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
		System.out.println("state is "+SL.toStr(state)+" and size "+sizex);
		this.observers = new LinkedList<Observer>();	

		super.fullName = super.getFullName(connectedNode);
		log.info(me+"Node configured and ready to provide simulator services!");
		this.registerSimulatorCommunication(connectedNode);
		this.waitForConnections(connectedNode);
	}

	protected int[] getStartingPosition(){
		return new int[]{(int)sizex/2, (int)sizey/2};	// start roughly in the center
	}

	// TODO define custom map here! override this..
	protected void defineMap(){
		// create map, place the reinforcements
		map = GridWorld.simpleRewardMap(sizex, sizey, null, 1);
		map[2][2] = rewardAVal;	// place reward A on the map
		map[5][5] = rewardBVal;	// place reward B on the map
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

						// move the agent
						int[] newState = executeMapAction(action);
						
						// read reward type on the current position
						float rewardA = 0, rewardB = 0;
						if(map[newState[0]][newState[1]] == rewardAVal)
							rewardA = DEF_REWARDVAL;
						else if(map[newState[0]][newState[1]] == rewardBVal)
							rewardB = DEF_REWARDVAL;

						std_msgs.Float32MultiArray fl = statePublisher.newMessage();
						// publish vector of floats, where two first values are two rewards
						fl.setData(encodeStateRewardMessage(rewardA, rewardB ,newState));								
						statePublisher.publish(fl);	// send a response with reinforcement and new state 
						state = newState.clone();

						//System.out.println("publishing "+SL.toStr(encodeStateRewardMessage(rewardA, rewardB ,newState)));
						
						if((step++)%logPeriod==0){
							System.out.println(me+"Responding with this state "+SL.toStr(newState)+
									" .. that is "+SL.toStr(encodeStateRewardMessage(rewardA, rewardB,newState)));
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
					fl.setData(encodeStateRewardMessage(0,0,state)); // state vars. to float[] 
					statePublisher.publish(fl);
				}
				dataExchanged = false;
			}
		});
	}
	
	/**
	 * Encode the description on a current state to agent. This is represented by vector of length 4.
	 * 
	 * @param rewardA vector[0] - reward of type A 
	 * @param rewardB vector[1] - reward of type B
	 * @param vars	- vector[2] and vector[3] - X and Y coordinates
	 * @return vector
	 */
	protected float[] encodeStateRewardMessage(float rewardA, float rewardB, int[] vars){
		float[] f = new float[vars.length+2];
		f[0] = rewardA;
		f[1] = rewardB;
		for(int i=2; i<=vars.length+1; i++){
			f[i] = stateEncoder.encode(vars[i-2]); 
		}
		return f;
	}

}
