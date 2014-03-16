package org.hanns.rl.discrete.ros.asm;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.ImportanceEpsGreedyFloat;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.util.SL;

/**
 * Similar to the {@link org.hanns.rl.discrete.ros.asm.EpsilonGreedy}, but here, the Epsilon
 * parameter is based on the action importance: the bigger importance the smaller randomization (epsilon).
 * 
 * @author Jaroslav Vitku
 */
public class ImportanceEpsilonGreedy extends AbstractASMNode{

	/**
	 * Amount of randomization in the ASM (how important the proper selection of action is?)
	 */
	public static final String importanceConf = "importance";
	public static final String topicImportance = conf+importanceConf;
	public static final double DEF_IMPORTANCE = ImportanceBasedConfig.DEF_IMPORTANCE;

	private ImportanceBasedConfig config;
	private ImportanceEpsGreedyFloat selection;// action selection methods
	Float[] tmp;

	@Override
	protected void onNewDataReceived(float[] data) {

		for(int i=0; i<data.length; i++)	// TODO, make this nicer
			tmp[i] = data[i];

		int selected = selection.selectAction(tmp);

		if(logPeriod % step ==0)
			System.out.println("Received these utilities: "+SL.toStr(data)+" selecting the action no.: "+selected);

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
					logParamChange("RECEIVED change of value IMPORTANCE",
							config.getImportance(), data[0]);

					config.setImportance(data[0]);
				}
			}
		});
	}

	@Override
	protected void registerParameters(){
		super.registerParameters();

		paramList.addParam(importanceConf, ""+DEF_IMPORTANCE, "How important is selection of the optimal action?");
	}

	@Override()
	protected void parseParameters(ConnectedNode connectedNode) {
		/**
		 * Parse default parameters and call the {@link #initializeASM()} method  
		 */
		super.parseParameters(connectedNode);
		double importance = r.getMyDouble(importanceConf, DEF_IMPORTANCE);

		config.setImportance((float)importance);
	}
}
