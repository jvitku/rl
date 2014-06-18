package org.hanns.rl.discrete.ros.asm;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.EpsilonGreedyDouble;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

/**
 * Implementation of the Epsilon-Greedy ASM as a ROS node.
 *  
 * @author Jaroslav Vitku
 *
 */
public class EpsilonGreedy extends AbstractASM{

	/**
	 * Importance based Epsilon-greedy ASM configuration
	 */
	public static final String epsilonConf="epsilon"; // TOOD change minEpsilon
	public static final String topicEpsilon = conf+epsilonConf;
	public static final double DEF_EPSILON=0.6;
	
	public static final String name = "EpsilonGreedyASM";


	@Override
	protected void buildASMSumbscribers(ConnectedNode connectedNode) {
		
		Subscriber<std_msgs.Float32MultiArray> epsilonSub = 
				connectedNode.newSubscriber(topicEpsilon, std_msgs.Float32MultiArray._TYPE);

		epsilonSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Epsilon config: Received message has " +
							"unexpected length of"+data.length+"!");
				else{

					logParamChange("RECEIVED chage of value EPSILON",
							((EpsilonGreedyDouble)asm).getConfig().getEpsilon(),data[0]);
					((EpsilonGreedyDouble)asm).getConfig().setEpsilon(data[0]);
				}
			}
		});

	}

}
