package org.hanns.rl.discrete.ros.testnodes.benchmark;

import org.hanns.rl.discrete.ros.testnodes.BenchmarkGridWorldNode;
import org.hanns.rl.discrete.ros.testnodes.worlds.GridWorldObstacle;

/**
 * The same as {@linkBenchmarkGridWorldNodeC}, different map (size).
 *  
 * @author Jaroslav Vitku
 *
 */
public class BenchmarkGridWorldNodeD extends BenchmarkGridWorldNode{

	/**
	 * Define the map, and store it in the memory
	 */
	@Override
	protected void defineMap(){
		sizex = 15;
		sizey = sizex; 

		mapReward = 15;

		log.info(me+"Parameters parsed, creating the map of size: "+sizex+"x"+sizey+"\n\n");

		map = new float[sizex][sizey];
		map[2][3] = mapReward;
		//map[16][15] = mapReward;
		//map[16][4] = mapReward;

		GridWorldObstacle.drawObstacle(new int[]{4,4}, new int[]{0,3}, map);
		GridWorldObstacle.drawObstacle(new int[]{sizex-5,sizex-5}, new int[]{sizey-4,sizey-1}, map);

		//GridWorldObstacle.drawObstacle(new int[]{3,3}, new int[]{0,2}, map);
		//GridWorldObstacle.drawObstacle(new int[]{6,6}, new int[]{sizey-3,sizey-1}, map);
		
		System.out.println("--------------- " +GridWorldObstacle.vis(map));
	}

}
