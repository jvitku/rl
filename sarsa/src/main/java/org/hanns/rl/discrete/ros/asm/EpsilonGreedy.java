package org.hanns.rl.discrete.ros.asm;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.BasicConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.EpsilonGreedyFloat;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

/**
 * Randomized version of the {@link org.hanns.rl.discrete.ros.asm.Greedy} ASM:
 * 
 * <ul>
 * <li>Random action: P(random) = Epsilon</li>
 * <li>Greedy action: P(greedy) = 1-Epsilon</ul> 
 * </ul>
 * 
 * @author Jaroslav Vitku
 *
 */
public class EpsilonGreedy extends AbstractASMNode{

	// sets the Epsilon: higher Epsilon -> higher randomization
	public static final String epsilonConf = "epsilon";
	public static final String topicEpsilon =  conf+epsilonConf;
	public static final double DEF_EPSILON = BasicConfig.DEF_EPSILON;

	private BasicConfig config;
	private EpsilonGreedyFloat selection;// action selection methods
	Float[] tmp;

	@Override
	protected void onNewDataReceived(float[] data) {

		for(int i=0; i<data.length; i++)	// TODO, make this nicer
			tmp[i] = data[i];

		int selected = selection.selectAction(tmp);
		super.executeAction(selected);
	}

	/**
	 * Set Epsilon configuration subscriber
	 */
	@Override
	protected void buildConfigSubscribers(ConnectedNode connectedNode){
		/**
		 * Epsilon
		 */
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
							config.getEpsilon(),data[0]);
					config.setEpsilon(data[0]);
				}
			}
		});
	}
	
	@Override()
	protected void parseParameters(ConnectedNode connectedNode) {
		/**
		 * Parse default parameters and call the {@link #initializeASM()} method  
		 */
		super.parseParameters(connectedNode);
		
		double epsilon = r.getMyDouble(epsilonConf, DEF_EPSILON);
		config.setEpsilon(epsilon);
	}
	
	@Override
	protected void registerParameters() {
		super.registerParameters();
		paramList.addParam(epsilonConf,""+DEF_EPSILON,"The higher epsilon, the higher randomization ASM uses.");
	}


	@Override
	protected void initializeASM() {

		config = new BasicConfig();
		selection = new EpsilonGreedyFloat(this.actions, config);
		asm = selection;	// handled in the parent
		
		tmp = new Float[this.actions.getNumOfActions()];
	}

}

