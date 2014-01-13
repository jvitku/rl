package org.hanns.rl.discrete.ros.sarsa;

import org.apache.commons.logging.Log;
import org.hanns.rl.discrete.observer.stats.ProsperityObserver;
import org.hanns.rl.discrete.ros.Topic;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import ctu.nengoros.rosparam.impl.PrivateRosparam;
import ctu.nengoros.rosparam.manager.ParamList;

/**
 * Defines ROS node with inputs and outputs with main purpose of use in the Hybrid 
 * Artificial Neural Network Systems (HANNS) framework.
 *  
 * @author Jaroslav Vitku
 *
 */
public abstract class AbstractHannsNode extends AbstractNodeMain {
	
	/**
	 * ROS node configuration
	 */
	public static final String name = "AbstractHannsNode"; // redefine the nodes name
	public final String me = "["+name+"] ";
	public static final String s = "/";
	public static final String ns = name+s; // namespace for config. parameters

	protected Log log;
	protected Publisher<std_msgs.Float32MultiArray> actionPublisher;
	
	// actions
	public static final String actionPrefix = "a";	// action names: a0, a1,a2,..
	public static final String topicDataOut = Topic.baseOut+"Actions"; 	// outActions
	
	// states
	public static final String statePrefix = "s"; 	// state var. names: s0,s1,..
	public static final String topicDataIn  = Topic.baseIn+"States"; 	// inStates

	// ROS node configurable parameters
	protected PrivateRosparam r;				// parameter (command-line) reader
	protected ParamList paramList;			// parameter storage
	
	/**
	 * Logging
	 */
	// whether to log (into the console)
	public static final String shouldLog = "shouldLog";
	public static final boolean DEF_LOG = true;
	protected boolean willLog = DEF_LOG;
	
	// how often to log data (into the console)
	public static final int DEF_LOGPERIOD =100;	// how often to log? 
	public static final String logPeriodConf = "logPeriod";
	protected int logPeriod = DEF_LOGPERIOD;
	
	// each node should be able to publish its prosperity (real time visible in the Nengoros)
	protected Publisher<std_msgs.Float32MultiArray> prospPublisher;
	public static final String topicProsperity = ns+"prosperity";

	/**
	 * IO configuration
	 */
	// Number of state variables considered by the RL (predefined sampling)
	public static final String noInputsConf = "noInputs";
	public static final int DEF_NOINPUTS = 2;
	// Number of actions that can be performed by the RL ASM (coding 1ofN)
	public static final String noOutputsConf = "noOutputs";
	public static final int DEF_NOOUTPUTS = 4;

	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	/**
	 * Main entry point to the ROS node. The initialization is made here, 
	 * the functionality of the node is called asynchronously by incoming 
	 * ROS messages.
	 * 
	 * @param connectedNode publisher/subscriber/log factory
	 */
	@Override
	public abstract void onStart(final ConnectedNode connectedNode);

	/**
	 * Read private parameters potentially passed to the node.
	 * These can be passed either from the command-line, or can be already
	 * set in the parameter server. 
	 * 
	 * Parse available parameters (or read default values) and instantiate
	 * all classes used here. 
	 * 
	 * @param connectedNode publisher/subscriber/log factory
	 */
	protected abstract void parseParameters(ConnectedNode connectedNode);
	
	/**
	 * Register parameters that are available for this node. 
	 * TOTO: the {@link ctu.nengoros.rosparam.manager.ParamList} should be able
	 * to parse the Nodes parameters from the XML file.
	 */
	protected abstract void registerParameters();
	
	/**
	 * Register some data publisher for publishing nodes data (actions),  
	 * and some data subscribers for receiving data expected by this node.
	 * 
	 * @param connectedNode ROS factory for publishers/subscribers
	 */
	protected abstract void buildDataIO(ConnectedNode connectedNode);
	
	/**
	 * The same as {@link #buildDataIO(ConnectedNode)}, but here, the 
	 * configuration subscribers (used for online node configuration) 
	 * can be registered.
	 * @param connectedNode ROS factory for registering publishers/subscribers
	 */
	protected abstract void buildConfigSubscribers(ConnectedNode connectedNode);
	
	/**
	 * Log to ROS network at any time, but only if the logging is allowed.
	 * @param what what to print out to the ROS console
	 */
	protected void myLog(String what){
		if(this.willLog)
			log.info(what);
	}

	/**
	 * Log only if allowed, and if the value is changed
	 * 
	 * @param message message to show value change
	 * @param oldVal old value
	 * @param newVal new one
	 */
	protected void logParamChange(String message, double oldVal, double newVal){
		if(!this.willLog)
			return;
		if(oldVal==newVal)
			return;
		log.info(message+" Value is being changed from: "+oldVal+" to "+newVal);
	}
	
	/**
	 * Each HannsNode should be able to provide (at least one-dimensional)
	 * information about its prosperity (in ideal case from the interval [0,1]). 
	 * 
	 * @return {@link ProsperityObserver} which defines how good a node performs, 
	 * observer has one value of prosperity, but can have also children observers.
	 */
	public abstract ProsperityObserver getProsperityObserver();
	
	/**
	 * By default, each HANNS node publishes one value of prosperity
	 * on the topic named prosperity. Node can publish more values, but
	 * the first one should be the combined (entire) prosperity by convention.
	 * @param connectedNode ROS factory for publishers/subscribers
	 */
	public void buildProsperityPublisher(ConnectedNode connectedNode){
		prospPublisher =connectedNode.newPublisher(topicProsperity, 
				std_msgs.Float32MultiArray._TYPE);
	}
	
	/**
	 * This method should use the {@link #prospPublisher} initialized by the
	 * {@link #buildProsperityPublisher(ConnectedNode)} to publish 
	 * its value(s) of prosperity over the ROS network.
	 */
	protected abstract void publishProsperity();
	
}
