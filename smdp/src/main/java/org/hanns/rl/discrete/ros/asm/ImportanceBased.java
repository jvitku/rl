package org.hanns.rl.discrete.ros.asm;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

public class ImportanceBased extends AbstractASM{

	public static final String name = "ImportanceBasedASM";

	/**
	 * Importance based Epsilon-greedy ASM configuration
	 */
	// probability of choosing action randomly
	//public static final String epsilonConf="epsilon"; // TOOD change minEpsilon
	//public static final String topicEpsilon = conf+epsilonConf;
	//public static final double DEF_EPSILON=0.6;
	// importance affect current value of epsilon: higher action importance, smaller eps.
	public static final String importanceConf = "importance";
	public static final String topicImportance = conf+importanceConf;
	public static final double DEF_IMPORTANCE = ImportanceBasedConfig.DEF_IMPORTANCE;


	@Override
	protected void buildASMSumbscribers(ConnectedNode connectedNode){
		/**
		 * Importance parameter
		 */
		Subscriber<std_msgs.Float32MultiArray> importenceSub = 
				connectedNode.newSubscriber(topicImportance, std_msgs.Float32MultiArray._TYPE);

		importenceSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Importance input: Received message has " +
							"unexpected length of"+data.length+"!");
				else{
					logParamChange("RECEIVED chage of value IMPORTANCE",
							((ImportanceBasedConfig)asm.getConfig()).getImportance(), data[0]);

					((ImportanceBasedConfig)asm.getConfig()).setImportance(data[0]);
				}
			}
		});
	}


}
