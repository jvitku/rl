package org.hanns.rl.discrete.ros.testnodes;

import org.hanns.rl.discrete.ros.testnodes.worlds.GridWorldObstacle;
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

		this.registerROSCommunication(connectedNode);

		this.defineMap();
		this.initData();

		state = new int[]{(int)sizex/2, (int)sizey/2};	// start roughly in the center

		log.info(me+"Node configured and ready to provide simulator services!");
		this.waitForConnections(connectedNode);
	}
	@Override
	protected void parseParameters(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		paramList = new ParamListTmp();

		paramList.addParam(logPeriodConf, ""+DEF_LOGPERIOD, "How often to log data to console?");
		logPeriod = r.getMyInteger(logPeriodConf, DEF_LOGPERIOD);
		
	}

	@Override
	protected int[] executeMapAction(int action){
		int[] newState = GridWorldObstacle.makeStep(map, action, state);
		return newState;
	}

	@Override
	protected void visMap(){
		System.out.println(GridWorldObstacle.vis(map));
	}


	/**
	 * Define the map, and store it in the memory
	 */
	@Override
	protected void defineMap(){
		sizex = 20;
		sizey = sizex; 

		mapReward = 1;

		log.info(me+"Parameters parsed, creating the map of size: "+sizex+"x"+sizey+"\n\n");

		map = new float[sizex][sizey];
		map[4][6] = mapReward;
		map[15][12] = mapReward;

		GridWorldObstacle.drawObstacle(new int[]{7,7}, new int[]{0,6}, map);
		GridWorldObstacle.drawObstacle(new int[]{12,12}, new int[]{sizey-7,sizey-1}, map);

		System.out.println("--------------- " +GridWorldObstacle.vis(map));
	}


}
