package org.hanns.rl.discrete.ros.asm;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.ImportanceEpsGreedyFloat;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

/**
 * Similar to the {@link org.hanns.rl.discrete.ros.asm.EpsilonGreedy}, but here, the Epsilon
 * parameter is based on the action importance: the bigger importance the smaller randomization (epsilon).
 * 
 * @author Jaroslav Vitku
 */
public class ImportanceEpsilonGreedy extends AbsImportanceBasedASMNode{

	private ImportanceBasedConfig config;
	private ImportanceEpsGreedyFloat selection;// action selection methods
	Float[] tmp;

	@Override
	protected void onNewDataReceived(float[] data) {

		for(int i=0; i<data.length; i++)	// TODO, make this nicer
			tmp[i] = data[i];

		int selected = selection.selectAction(tmp);
		super.executeAction(selected);
	}

	@Override
	protected void initializeASM() {

		config = new ImportanceBasedConfig();
		selection = new ImportanceEpsGreedyFloat(this.actions, config);
		asm = selection;	// handled in the parent
		
		tmp = new Float[this.actions.getNumOfActions()];
	}
	
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
}
