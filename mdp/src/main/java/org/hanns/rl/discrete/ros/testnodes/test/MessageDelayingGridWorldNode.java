package org.hanns.rl.discrete.ros.testnodes.test;

import org.hanns.rl.common.exceptions.DecoderException;
import org.hanns.rl.discrete.ros.sarsa.QLambda;
import org.hanns.rl.discrete.ros.testnodes.GridWorldNode;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import java.util.*;

import ctu.nengoros.util.SL;

/**
 * The same as GridWorldNode, but this waits N steps before sending the state, for
 * N=1 this simulates traffic delay which occurs in the Nengoros.
 * 
 * @author Jaroslav Vitku
 *
 */
public class MessageDelayingGridWorldNode extends GridWorldNode{

	public static final String delayConf = "delay";
	public static final int DEF_DELAY = 1;	// simulate the Nengoros delay by default
	protected int delay;
	
	protected LinkedList<float[]> messages;
	
	public GraphName getDefaultNodeName() { return GraphName.of("MessageDelayingGridWorld"); }

	public void setDelay(int d){
		if(d<0){
			System.err.println("MessageDelay: no under zero");
			d = 0;
		}
		this.delay = d;
		this.messages = new LinkedList<float[]>();
	}
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();
		log.info(me+"started, parsing parameters. \n\nInfo: \n-This is simple 2D grid world."
				+ "\n-An agent has four actions: {<,>,^,v}."
				+ "\n-Each tale defines vlaue of reinforcement (mostly zeros)"
				+ "\n-By stepping on a tale, the reinforcement (value of a tale) is received."
				+ "\n-This node is subscribed to agents actions, it responds with "
				+ "a reinforcement and a new state immediatelly after receiving the action."
				+ "\n-Response is composed as follows: [float reward, float varX, float varY]\n\n"
				+ "Note that this node delays state(reward) response for N steps (for testing)");

		this.parseParameters(connectedNode);
		this.printParams();
		log.info(me+"Parameters parsed, creating the map of size: "+sizex+"x"+sizey+"\n\n");

		this.registerROSCommunication(connectedNode);

		this.defineMap();
		this.initData();

		state = new int[]{(int)sizex/2, (int)sizey/2};	// start roughly in the center

		log.info(me+"Node configured and ready to provide simulator services!");
		this.waitForConnections(connectedNode);
	}

	@Override
	protected void initData(){
		super.initData();
		
		this.setDelay(delay); // init the linked list

	}
	
	/**
	 * Delays message for {@link #delay} steps, pass new message, returns message to be sent
	 * @param message new message to be delayed
	 * @return old message to be sent immediately 
	 */
	protected float[] delayMessage(float[] message){
		
		messages.addFirst(message.clone());
		
		if(messages.size() <= this.delay){
			return this.emptyMessage(message);
		}
		float[] out = messages.removeLast();
		return out;
	}

	protected float[] emptyMessage(float[] anyMessage){
		float[] out = anyMessage.clone();
		for(int i=0; i<out.length; i++)
			out[i] = 0;
		return out;
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
						
						/**
						 * Delaying goes here
						 */
						float[] currentMessage = encodeStateRewardMessage(reward,newState);
						float[] messToSend = delayMessage(currentMessage);	// delay it
						fl.setData(messToSend);
						/*
						System.out.println("message is now: "+SL.toStr(currentMessage)+
								" but will be sent this "+SL.toStr(messToSend));
						try {
							System.in.read();
						} catch (IOException e) {
							e.printStackTrace();
						}*/
						
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


	@Override
	protected void parseParameters(ConnectedNode connectedNode){
		super.parseParameters(connectedNode);

		// parse the number of steps to wait before responding with the state / reward message 
		delay= r.getMyInteger(delayConf, DEF_DELAY);
		
		paramList.addParam(delayConf, ""+DEF_DELAY, "number of steps to wait before responding "
				+ "with the state / reward message");
	}

}
