package org.hanns.rl.discrete.ros.asm;

import java.util.LinkedList;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.network.node.AbstractConfigurableHannsNode;
import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;
import ctu.nengoros.util.SL;

/**
 * ROS node for testing, this receives the selected action and checks the 
 * correctness of the selection.
 *  
 * @author Jaroslav Vitku
 */
public class ActionReceiver extends AbstractConfigurableHannsNode{

	private boolean started = false;
	protected int noActions = 4; // e.g. 4 actions
	protected int step = 0;

	public void setNoActions(int actions){ this.noActions = actions; }

	public int getNoActions(){ return this.noActions; }



	@Override
	public void onStart(ConnectedNode connectedNode) {

		System.out.println(me+"initializing ROS Node IO");

		this.buildDataIO(connectedNode);

		System.out.println(me+"Node configured and ready now!");
		started = true;
	}

	private float[] receivedData;
	private int sleep = 10;
	private int maxSleep = 5000;	
	boolean arrived; 

	/**
	 * Set array of action utilities, the method returns remote solution 
	 * (that is action selected by the corresponding ASM node). 
	 * @param actions array of action utilities (random numbers)
	 * @return response should contain result of ASM: 1ofN encoding of selected action
	 */
	public float[] getResponseFor(float[] actions){
		
		if(actions.length!=this.noActions){
			System.err.println("Incorrect length of  message array, expected: "+this.noActions);
			return null;
		}
		arrived = false;
		int slept = 0;

		std_msgs.Float32MultiArray fl = dataPublisher.newMessage();	
		fl.setData(actions);								
		dataPublisher.publish(fl);

		while(!arrived){
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) { e.printStackTrace(); }
			if(slept>maxSleep){
				System.err.println("Waited too long for message");
				return null;
			}
		}
		return receivedData;
	}

	@Override
	public String listParams() {
		return null;
	}

	@Override
	public String getFullName() { return "ActionReceiver"; }

	@Override
	public boolean isStarted() { return this.started; }

	@Override
	public void hardReset(boolean arg0) {
		step = 0;
	}

	@Override
	public void softReset(boolean arg0) {
		step = 0;
	}

	@Override
	public LinkedList<Observer> getObservers() {
		return null;
	}

	@Override
	public float getProsperity() {
		return 0;
	}

	@Override
	public void publishProsperity() {
	}

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("ActionReceiver"); }

	@Override
	protected void buildConfigSubscribers(ConnectedNode arg0) {
	}

	/**
	 * Topics are switched to be able to directly (ROS msgs) connect to the ASM node. 
	 */
	@Override
	protected void buildDataIO(ConnectedNode connectedNode) {

		dataPublisher =connectedNode.newPublisher(Greedy.topicDataIn, std_msgs.Float32MultiArray._TYPE);

		Subscriber<std_msgs.Float32MultiArray> dataSub = 
				connectedNode.newSubscriber(Greedy.topicDataOut, std_msgs.Float32MultiArray._TYPE);

		dataSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != noActions)
					log.error(me+":"+Greedy.topicDataIn+": Received array of action utilities has" +
							"unexpected length of"+data.length+"! Expected: "+noActions);
				else{
					// here, the state description is decoded and one SARSA step executed
					if(++step % logPeriod==0)
						System.out.println(me+"<-"+Greedy.topicDataIn+" SENDING array of action" +
								" utilities, these are: "+SL.toStr(data));

					// implement this
					receivedData = data.clone();
					arrived = true;
				}
			}
		});
	}

	@Override
	public ProsperityObserver getProsperityObserver() {
		return null;
	}

	@Override
	protected void parseParameters(ConnectedNode arg0) {
	}

	@Override
	protected void registerParameters() {
	}

}
