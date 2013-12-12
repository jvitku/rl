package ctu.hanns.logic.gates;

import org.apache.commons.logging.Log;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import std_msgs.Bool;

/**
 * @author Jaroslav Vitku vitkujar@fel.cvut.cz
 * 
 */
public abstract class MisoGate extends AbstractNodeMain {

	// node setup
	private final boolean SEND = false;
	private final int sleepTime = 1000;

	// ROS stuff
	Subscriber<std_msgs.Bool> subscriberA, subscriberB;
	Publisher<std_msgs.Bool> publisher;
	Log log;
	
	public final String aT = "logic:gates:ina";
	public final String bT = "logic:gates:inb";
	public final String yT = "logic:gates:outa";

	private boolean a = false,b = false, y=false;
	private volatile boolean inited = false;
	
	/**
	 * implement this in order to make computation 
	 * @param a input value A
	 * @param b input value B
	 * @return output value Y
	 */
	protected abstract boolean copute(boolean a, boolean b);

	private void send(){
		if(!inited)
			return;
		
		std_msgs.Bool out = publisher.newMessage();
		out.setData(y);
		publisher.publish(out);
		log.info("RECEIVED!! Publishing this: \"" + out.getData() + " !! on topic: "+yT);
	}
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();
		
		// register subscribers
		subscriberA = connectedNode.newSubscriber(aT, std_msgs.Bool._TYPE);
		subscriberB = connectedNode.newSubscriber(bT, std_msgs.Bool._TYPE);

		subscriberA.addMessageListener(new MessageListener<std_msgs.Bool>() {
			@Override
			public void onNewMessage(Bool message) {
				a = message.getData();
				y = copute(a,b);
				send();
			}
		});
		subscriberB.addMessageListener(new MessageListener<std_msgs.Bool>() {
			@Override
			public void onNewMessage(Bool message) {
				b = message.getData();
				y = copute(a,b);
				send();
			}
		});

		// register publisher
		publisher = connectedNode.newPublisher(yT, std_msgs.Bool._TYPE);		
		inited = true;

		
		// infinite loop
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			@Override
			protected void setup() {	
			}

			@Override
			protected void loop() throws InterruptedException {

				if(SEND){
					std_msgs.Bool out = publisher.newMessage();
					out.setData(y);
					publisher.publish(out);
					log.info("Publishing this: \"" + out.getData() + " !! on topic: "+yT);
				}
				Thread.sleep(sleepTime);
			}
		});
	}
}
