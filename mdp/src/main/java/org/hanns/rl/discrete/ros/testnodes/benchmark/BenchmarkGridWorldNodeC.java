package org.hanns.rl.discrete.ros.testnodes.benchmark;

import org.hanns.rl.discrete.ros.testnodes.BenchmarkGridWorldNode;
import org.hanns.rl.discrete.ros.testnodes.worlds.GridWorldObstacle;

/**
 * The same as {@linkBenchmarkGridWorldNodeB}, different map (size).
 *  
 * @author Jaroslav Vitku
 *
 */
public class BenchmarkGridWorldNodeC extends BenchmarkGridWorldNode{

	/**
	 * Define the map, and store it in the memory
	 */
	@Override
	protected void defineMap(){
		sizex = 20;
		sizey = sizex; 

		mapReward = 15;

		log.info(me+"Parameters parsed, creating the map of size: "+sizex+"x"+sizey+"\n\n");

		map = new float[sizex][sizey];
		map[3][4] = mapReward;
		//map[16][15] = mapReward;
		//map[16][4] = mapReward;

		GridWorldObstacle.drawObstacle(new int[]{6,6}, new int[]{0,5}, map);
		GridWorldObstacle.drawObstacle(new int[]{sizex-6,sizex-6}, new int[]{sizey-5,sizey-1}, map);

		//GridWorldObstacle.drawObstacle(new int[]{3,3}, new int[]{0,2}, map);
		//GridWorldObstacle.drawObstacle(new int[]{6,6}, new int[]{sizey-3,sizey-1}, map);
		
		System.out.println("--------------- " +GridWorldObstacle.vis(map));
	}

}


