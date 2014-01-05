package org.hanns.rl.discrete.ros.testnodes;

import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

/**
 * Provides very similar map to the one from src/test/java (used for testing) in for of a ROS node
 * compatible with RL ROS nodes. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class GridWorldNode extends AbstractNodeMain{
	
	public static final String name = "GridWorldNode";
	public final String me = "["+name+"]";

	public static final String shouldLog = "shouldLog";
	public static final boolean DEF_LOG = true;
	private boolean willLog = true;
	private Log log;
	private Publisher<std_msgs.Float32MultiArray> statePublisher;
	private Publisher<std_msgs.Float32MultiArray> actionSubscriber;
	
	

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();

		/*
		log.info(me+"started, parsing parameters");
		this.parseParameters(connectedNode);

		myLog(me+"initializing ROS Node IO");
		this.buildASMSumbscribers(connectedNode);
		this.buildEligibilitySubscribers(connectedNode);
		this.buildRLSubscribers(connectedNode);
		this.buildDataIO(connectedNode);

		myLog(me+"Node configured and ready now!");*/

	}


}
