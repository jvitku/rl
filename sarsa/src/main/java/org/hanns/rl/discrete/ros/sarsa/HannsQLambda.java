package org.hanns.rl.discrete.ros.sarsa;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.ImportanceEpsGreedyDouble;
import org.hanns.rl.discrete.observer.Observer;
import org.hanns.rl.discrete.observer.impl.BinaryCoverageForgettingReward;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
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
	
	protected Publisher<std_msgs.Float32MultiArray> prosperityPub;

	public static final String importanceConf = "importance";
	public static final String topicImportance = ns+importanceConf;
	public static final double DEF_IMPORTANCE = 0.7; 
	
	Observer o;	// observes the prosperity of node
		
	@Override
	protected void performSARSAstep(float reward, float[] state){
		// store the data into the int[]states
		super.decodeState(state);	
		// choose action and learn about it
		int action = super.learn(reward); 
		// use observer to log info
		o.observe(super.prevAction, reward, states.getValues(), action);
		// execute action
		super.executeAction(action);
	}
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		
		log = connectedNode.getLog();
		
		this.addParams();
		paramList.addParam(importanceConf, ""+DEF_IMPORTANCE, "How important is action selected "
				+ "(less important means more expliration)");
		this.printParams();
		log.info(me+"started, parsing parameters");
		this.parseParameters(connectedNode);
		//o = new KnowledgeCoverageReward(states.getDimensionsSizes(),q);
		o = new BinaryCoverageForgettingReward(states.getDimensionsSizes());//,q);
		
		myLog(me+"initializing ROS Node IO");
		this.buildASMSumbscribers(connectedNode);
		this.buildEligibilitySubscribers(connectedNode);
		this.buildRLSubscribers(connectedNode);
		this.buildDataIO(connectedNode);
		this.buildProsperityPublisher(connectedNode); // this is added
		
		myLog(me+"Node configured and ready now!");
	}
	
	@Override
	protected void initializeASM(double epsilon){
		/**
		 *  configure the ASM
		 */
		ImportanceBasedConfig asmConf = new ImportanceBasedConfig();
		asm = new ImportanceEpsGreedyDouble(actions, asmConf);
		asm.getConfig().setExplorationEnabled(true);
	}
	
	protected void buildProsperityPublisher(ConnectedNode connectedNode){
		actionPublisher =connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE); 
	}
	
	protected void publishProsperity(){
		std_msgs.Float32MultiArray fl = prosperityPub.newMessage();	
		fl.setData(new float[]{this.getProsperity()});								
		prosperityPub.publish(fl);
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
	public float getProsperity() { return o.getProsperity(); }

	@Override
	public void setImportance(float importance) {
		logParamChange("CALLED method to chage the value IMPORTANCE",
				((ImportanceBasedConfig)asm.getConfig()).getImportance(), importance);
		
		((ImportanceBasedConfig)asm.getConfig()).setImportance(importance);	
	}
	
	@Override
	public String listParams() { return super.paramList.listParams(); }

	
}
