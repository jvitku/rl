package org.hanns.rl.discrete.ros.sarsa;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.ImportanceEpsGreedyDouble;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.nodes.HannsNode;

/**
 * The same as {@link QLambda}, but here, the support for HannsNode (importance 
 * setting and quality measure) is added.
 * 
 * The implementation of HannsNode has two main features here:
 * <ul>
 * <li>The methods are available as usual</li>
 * <li>The data are accessible (subscribe-importance/publish-prosperity) in the
 * ROS network.</li>
 * </ul>
 * 
 * @author Jaroslav Vitku
 *
 */
public class HannsQLambda extends QLambda implements HannsNode{


	public static final String name = "QLambda";

	public static final String importanceConf = "importance";
	public static final String topicImportance = ns+importanceConf;

	@Override
	protected void initializeASM(double epsilon){
		/**
		 *  configure the ASM
		 */
		ImportanceBasedConfig asmConf = new ImportanceBasedConfig();
		asm = new ImportanceEpsGreedyDouble(actions, asmConf);
		asm.getConfig().setExplorationEnabled(true);
	}
	
	@Override
	protected void buildASMSumbscribers(ConnectedNode connectedNode){
		/**
		 * Subscribe to Importance parameter
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
	public void setImportance(float importance) {
		logParamChange("CALLED method to chage the value IMPORTANCE",
				((ImportanceBasedConfig)asm.getConfig()).getImportance(), importance);
		
		((ImportanceBasedConfig)asm.getConfig()).setImportance(importance);	
	}
}
