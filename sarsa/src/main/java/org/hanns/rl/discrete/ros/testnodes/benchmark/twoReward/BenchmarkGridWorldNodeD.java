package org.hanns.rl.discrete.ros.testnodes.benchmark.twoReward;

import org.hanns.rl.discrete.ros.testnodes.TwoRewardGridWorldNode;
import org.hanns.rl.discrete.ros.testnodes.worlds.GridWorldObstacle;

/**
 * The same as {@linkBenchmarkGridWorldNodeC}, but here, two types of reward are produced
 * 
 * TODO not tested well so far (agent learns also on the walls)
 * 
 * @see org.hanns.rl.discrete.ros.testnodes.TwoRewardGridWorldNode
 * 
 * @author Jaroslav Vitku
 *
 */
public class BenchmarkGridWorldNodeD extends TwoRewardGridWorldNode{

	@Override
	protected void defineMap(){
		sizex = 20;
		sizey = sizex; 

		log.info(me+"Parameters parsed, creating the map of size: "+sizex+"x"+sizey+"\n\n");

		map = new float[sizex][sizey];

		/**
		 * Obstacles identical to the BenchmarkGridWorldNodeD
		 */
		GridWorldObstacle.drawObstacle(new int[]{6,6}, new int[]{0,5}, map);
		GridWorldObstacle.drawObstacle(new int[]{sizex-7,sizex-7}, new int[]{sizey-5,sizey-1}, map);
		
		// two types of reward here 
		map[3][4] 	= rewardAVal;	// place reward A on the map
		map[16][15] = rewardBVal;	// place reward B on the map
		
		System.out.println("--------------- " +GridWorldObstacle.vis(map));
	}
	

}
