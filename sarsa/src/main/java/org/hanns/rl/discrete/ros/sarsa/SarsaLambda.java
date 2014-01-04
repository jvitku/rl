package org.hanns.rl.discrete.ros.sarsa;

import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

import ctu.nengoros.rosparam.impl.PrivateRosparam;

/**
 * Get vector of 4 floats, select min and max and publish them.
 * 
 * @author Jaroslav Vitku
 *
 */
public class SarsaLambda extends AbstractNodeMain {

	public static final String name = "SarsaLambda";
	public final String me = "["+name+"] ";
	private PrivateRosparam r;

	/**
	 * Learning rate
	 */
	public static final String alphaConfig = "alpha";
	public static final double DEF_ALPHA = 0.6;
	private double alpha;
	
	/**
	 * Discount factor
	 */
	public static final String gammaConfig = "gamma";
	public static final double DEF_GAMMA = 0.4;
	private double gamma;
	
	/**
	 * Number of state variables considered by the RL (predefined sampling)
	 */
	public static final String noInputs = "noInputs";
	public static final int DEF_STATEVARS = 2;
	private int noStateVars;
	
	/**
	 * Number of actions that can be performed by the RL ASM (coding 1ofN)
	 */
	public static final String noOutputs = "noOutputs";
	public static final int DEF_NOACTIONS = 2;
	private int noActions;
	
	public static final String shouldLog = "shouldLog";
	public static final boolean DEF_LOG = true;
	private boolean willLog = true;
	private Log log;

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();
		log.info(me+"started, initalizing IO");


		// read parameters
		this.configureNode(connectedNode);


		log.info(me+"Node configured and ready now!");
	}
	

	/**
	 * Read private parameters potentially passed to the node. 
	 */
	private void configureNode(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		
		alpha = r.getMyDouble(alphaConfig, DEF_ALPHA);
		gamma = r.getMyDouble(gammaConfig, DEF_GAMMA);
		
		noStateVars = r.getMyInteger(noInputs, DEF_STATEVARS);
		noActions = r.getMyInteger(noOutputs, DEF_NOACTIONS);
		
		willLog = r.getMyBoolean(shouldLog, DEF_LOG);
		
	}


}
