package org.hanns.rl.discrete.ros.testnodes;

import org.ros.node.ConnectedNode;

import ctu.nengoros.rosparam.impl.PrivateRosparam;
import ctu.nengoros.rosparam.manager.ParamListTmp;

/**
 * The same as GrodWorldNode, but this one has predefined map with obstacles and rewards.
 * 
 * @author Jaroslav Vitku
 *
 */
public class BenchmarkGridWorldNode extends GridWorldNode{
	
	public final int sizeXmap = 30;
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();
		log.info(me+"started, parsing parameters. \n\nInfo: \n-This is simple 2D grid world."
				+ "\n-An agent has four actions: {<,>,^,v}."
				+ "\n-Each tale defines vlaue of reinforcement (mostly zeros)"
				+ "\n-By stepping on a tale, the reinforcement (value of a tale) is received."
				+ "\n-This node is subscribed to agents actions, it responds with "
				+ "a reinforcement and a new state immediatelly after receiving the action.");
				//+ "\n-Response is composed as follows: [float reward, float varX, float varY]\n\n");

		this.parseParameters(connectedNode);
		this.printParams();
		log.info(me+"Parameters parsed, creating the map of size: "+sizex+"x"+sizey+"\n\n");

		this.registerROSCommunication(connectedNode);

		this.initData();

		state = new int[]{(int)sizex/2, (int)sizey/2};	// start roughly in the center

		log.info(me+"Node configured and ready to provide simulator services!");
		this.waitForConnections(connectedNode);
	}
	@Override
	protected void parseParameters(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		paramList = new ParamListTmp();

		sizex = sizeXmap;
		sizey = sizex; 
		
		paramList.addParam(logPeriodConf, ""+DEF_LOGPERIOD, "How often to log data to console?");
		logPeriod = r.getMyInteger(logPeriodConf, DEF_LOGPERIOD);
	}
}
