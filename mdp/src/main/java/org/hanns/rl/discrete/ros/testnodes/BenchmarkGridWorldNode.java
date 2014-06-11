package org.hanns.rl.discrete.ros.testnodes;

import org.hanns.rl.discrete.ros.testnodes.worlds.GridWorldObstacle;
import org.ros.node.ConnectedNode;

import ctu.nengoros.network.node.infrastructure.rosparam.impl.PrivateRosparam;
import ctu.nengoros.network.node.infrastructure.rosparam.manager.ParamList;

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
		this.registerSimulatorCommunication(connectedNode);
		this.waitForConnections(connectedNode);
	}
	
	@Override
	protected void parseParameters(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		paramList = new ParamList();

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
		sizex = 10;
		sizey = sizex; 

		mapReward = 15;

		log.info(me+"Parameters parsed, creating the map of size: "+sizex+"x"+sizey+"\n\n");

		map = new float[sizex][sizey];
		map[1][4] = mapReward;
		//map[15][12] = mapReward;

		//GridWorldObstacle.drawObstacle(new int[]{6,6}, new int[]{0,4}, map);
		//GridWorldObstacle.drawObstacle(new int[]{10,10}, new int[]{sizey-4,sizey-1}, map);

		GridWorldObstacle.drawObstacle(new int[]{3,3}, new int[]{0,2}, map);
		GridWorldObstacle.drawObstacle(new int[]{6,6}, new int[]{sizey-3,sizey-1}, map);
		
		System.out.println("--------------- " +GridWorldObstacle.vis(map));
	}

}

