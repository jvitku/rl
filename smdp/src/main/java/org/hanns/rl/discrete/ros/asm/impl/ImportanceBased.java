package org.hanns.rl.discrete.ros.asm.impl;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.ImportanceEpsGreedyDouble;
import org.hanns.rl.discrete.ros.asm.AbstractASMDouble;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

public class ImportanceBased extends AbstractASMDouble{

	public static final String name = "ImportanceBasedASM";

	private ImportanceBasedConfig config;

	/**
	 * Epsilon..
	 */
	// TODO: determine what is with the Min Epsilon here!
	//public static final String epsilonConf="epsilon"; // TOOD change minEpsilon
	//public static final String topicEpsilon = conf+epsilonConf;
	//public static final double DEF_EPSILON=0.6;

	/**
	 * Importance affects the current value of epsilon: higher action importance, smaller eps.
	 */
	public static final String importanceConf = "importance";
	public static final String topicImportance = conf+importanceConf;
	public static final double DEF_IMPORTANCE = ImportanceBasedConfig.DEF_IMPORTANCE;

	/**
	 * Initialize this ASM structures
	 */
	@Override
	protected void initializeASM(){

		config = new ImportanceBasedConfig();
		asm = new ImportanceEpsGreedyDouble(actions, config);
		asm.getConfig().setExplorationEnabled(true);

		// this forces the agent to use only greedy ASM when importance is 1 
		//((ImportanceEpsGreedyDouble)asm).getConfig().setMinEpsilon(0);
	}
	
	@Override
	protected void registerParameters(){
		super.registerParameters();
		paramList.addParam(importanceConf, ""+DEF_IMPORTANCE,"The higher importance, "
				+ "the lower the randomization");
	}

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

	@Override
	public boolean isStarted(){
		if(!super.isStarted())
			return false;
		if(config==null)
			return false;
		return true;
	}
}
