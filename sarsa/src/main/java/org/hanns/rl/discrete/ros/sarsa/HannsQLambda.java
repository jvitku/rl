package org.hanns.rl.discrete.ros.sarsa;

import org.hanns.rl.discrete.observer.stats.impl.BinaryCoverageForgettingReward;
import org.ros.node.ConnectedNode;

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
@Deprecated
public class HannsQLambda extends AbstractQLambda{
	/*
	@Override
	protected void performSARSAstep(float reward, float[] state){
		// store the data into the int[] states
		super.decodeState(state);	
		// choose action and learn about it
		int action = super.learn(reward); 
		// use observer to log info
		o.observe(super.prevAction, reward, states.getValues(), action);
		// execute action
		super.executeAction(action);
	}*/
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		
		log = connectedNode.getLog();
		
		this.registerParameters();
		paramList.addParam(importanceConf, ""+DEF_IMPORTANCE, "How important is action selected "
				+ "(less important means more expliration)");
		paramList.printParams();
		log.info(me+"started, parsing parameters");
		this.parseParameters(connectedNode);
		//o = new KnowledgeCoverageReward(states.getDimensionsSizes(),q);
		o = new BinaryCoverageForgettingReward(states.getDimensionsSizes());//,q);
		
		myLog(me+"initializing ROS Node IO");
		this.buildASMSumbscribers(connectedNode);
		this.buildEligibilitySubscribers(connectedNode);
		this.buildConfigSubscribers(connectedNode);
		this.buildDataIO(connectedNode);
		this.buildProsperityPublisher(connectedNode); // this is added
		
		myLog(me+"Node configured and ready now!");
	}

	@Override
	protected void onNewDataReceived(float[] data) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	protected void buildProsperityPublisher(ConnectedNode connectedNode){
		actionPublisher =connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE); 
	}
	*/
	
	

	
}
