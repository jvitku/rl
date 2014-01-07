package org.hanns.rl.discrete.ros.sarsa;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.EpsilonGreedyDouble;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.ImportanceEpsGreedyDouble;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

/**
 * The same as {@link QLambda}, but here, the support for HannsNode (importance 
 * setting and quality measure) is added.
 * 
 * @author Jaroslav Vitku
 *
 */
public class HannsQLambda extends QLambda{
	
	public static final String name = "HannsQLambda";
	
	@Override
	protected void initializeASM(double epsilon){
		/**
		 *  configure the ASM
		 */
		ImportanceBasedConfig asmConf = new ImportanceBasedConfig();
		asm = new ImportanceEpsGreedyDouble(actions, asmConf);
		//asm.getConfig().setEpsilon(epsilon);
		asm.getConfig().setExplorationEnabled(true);
	}
	
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
					logParamChange("RECEIVED chage of value Epsilon",
							((EpsilonGreedyDouble)asm).getConfig().getEpsilon(),data[0]);
					((EpsilonGreedyDouble)asm).getConfig().setEpsilon(data[0]);
				}
			}
		});
	}}
