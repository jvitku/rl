package org.hanns.rl.discrete.ros.testnodes.benchmark;

import org.hanns.rl.discrete.ros.testnodes.BenchmarkGridWorldNode;
import org.hanns.rl.discrete.ros.testnodes.worlds.GridWorldObstacle;

/**
 * GridWorld Node map with 30x30 tales and one attractor hidden behind the obstacle.
 * 
 * @author Jaroslav Vitku
 *
 */
public class BenchmarkGridWorldNodeB extends BenchmarkGridWorldNode{

	/**
	 * Define the map, and store it in the memory
	 */
	@Override
	protected void defineMap(){
		sizex = 30;
		sizey = sizex; 

		mapReward = 15;

		log.info(me+"Parameters parsed, creating the map of size: "+sizex+"x"+sizey+"\n\n");

		map = new float[sizex][sizey];
		map[5][5] = mapReward;
		map[25][25] = mapReward;

		GridWorldObstacle.drawObstacle(new int[]{11,11}, new int[]{0,7}, map);
		GridWorldObstacle.drawObstacle(new int[]{19,19}, new int[]{sizey-8,sizey-1}, map);

		//GridWorldObstacle.drawObstacle(new int[]{3,3}, new int[]{0,2}, map);
		//GridWorldObstacle.drawObstacle(new int[]{6,6}, new int[]{sizey-3,sizey-1}, map);
		
		System.out.println("--------------- " +GridWorldObstacle.vis(map));
	}

}
