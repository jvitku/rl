package org.hanns.rl.discrete.ros.asm;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

public class ImportanceEpsilonGreedy extends AbsImportanceBasedASMNode{

	@Override
	protected void buildConfigSubscribers(ConnectedNode connectedNode) {
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

	@Override
	public float getProsperity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void registerProsperityObserver() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initializeASM() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNewDataReceived(float[] data) {
		// TODO Auto-generated method stub
		
	}

}
