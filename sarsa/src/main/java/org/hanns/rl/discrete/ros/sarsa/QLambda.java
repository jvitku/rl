package org.hanns.rl.discrete.ros.sarsa;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.BasicConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.EpsilonGreedyDouble;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

/**
 * Implementation of Q(lambda) RL algorithm, which is usable a ROS node.
 * 
 * @author Jaroslav Vitku
 *
 */
public class QLambda extends AbstractQLambda{

	protected void initializeASM(double epsilon){
		/**
		 *  configure the ASM
		 */
		BasicConfig asmConf = new BasicConfig();
		asm = new EpsilonGreedyDouble(actions, asmConf);
		((EpsilonGreedyDouble)asm).getConfig().setEpsilon(epsilon);
		asm.getConfig().setExplorationEnabled(true);
	}

	@Override
	protected void buildASMSumbscribers(ConnectedNode connectedNode){
		/**
		 * Epsilon
		 */
		Subscriber<std_msgs.Float32MultiArray> epsilonSub = 
				connectedNode.newSubscriber(name+s+epsilonConf, std_msgs.Float32MultiArray._TYPE);

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
