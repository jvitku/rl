package org.hanns.rl.discrete.ros.asm.impl;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.BasicEpsilonGeedyConf;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.BasicConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.EpsilonGreedyDouble;
import org.hanns.rl.discrete.ros.asm.AbstractASMDouble;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

/**
 * Implementation of the Epsilon-Greedy ASM as a ROS node.
 * Compared to the Greedy node, this receives also epsilon.
 *  
 * @author Jaroslav Vitku
 *
 */
public class EpsilonGreedy extends AbstractASMDouble{

	/**
	 * Importance based Epsilon-greedy ASM configuration
	 */
	public static final String epsilonConf="epsilon"; // TODO change minEpsilon too
	public static final String topicEpsilon = conf+epsilonConf;
	public static final double DEF_EPSILON=0.6;

	public static final String name = "EpsilonGreedyASM";

	private BasicEpsilonGeedyConf config;

	/**
	 * Initialize this ASM structures
	 */
	@Override
	protected void initializeASM(){
		
		config = new BasicConfig();
		asm = new EpsilonGreedyDouble(actions, config);
		asm.getConfig().setExplorationEnabled(true);
		
		// this forces the agent to use only greedy ASM when importance is 1 
		//((ImportanceEpsGreedyDouble)asm).getConfig().setMinEpsilon(0);
	}

	@Override
	protected void registerParameters(){
		super.registerParameters();
		paramList.addParam(epsilonConf, ""+DEF_EPSILON,"Probability of randomization in the ASM");
	}
	
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

	@Override
	public boolean isStarted(){
		if(!super.isStarted())
			return false;
		if(config==null)
			return false;
		return true;
	}
}



