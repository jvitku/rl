package org.hanns.rl.discrete.q;

import java.util.Random;

import org.hanns.rl.discrete.learningAlgorithm.FinalModelLearningAlgorithm;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.config.impl.BasicConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.impl.FinalModelQlearning;
import org.junit.Test;

/**
 * Tests the FinalModelQLearning class.
 * 
 * @author Jaroslav Vitku
 *
 */
public class FinalQLearning {

	@Test
	public void basic(){
		Random r = new Random();

		int sx = 10;
		int sy = 10;
		int[] stateSizes = new int[]{sx,sy}; 
		int numActions = 4;		// left, right, up, down

		BasicConfiguration config = new BasicConfiguration();
		config.setAlpha(0.5);	// learn half of the information
		config.setGamma(0.7);	// more towards immediate reward

		//new FinalModelQlearning(int[] stateSizes, int numActions, QLearningConfig config){
		FinalModelLearningAlgorithm ql = new FinalModelQlearning(stateSizes, numActions, config);

		float[][] map = this.simpleRewardMap(sx, sy, new int[]{7,7}, 15);
		System.out.println("map generated is: \n"+this.vis(map));

		int[] pos = new int[]{2,2};	// agents position on the map
		int numsteps = 100000;
		int action;
		float reward;

		FinalQMatrix<Double> q = (FinalQMatrix<Double>)(ql.getMatrix());

		for(int i=0; i<numsteps; i++){
			action = this.generateAction(r, numActions);	// select action 
			pos = this.makeStep(sx, sy, action, pos);		// move agent
			reward = map[pos[0]][pos[1]];					// read reward
			ql.performLearningStep(action, reward, pos);	// learn about it
			System.out.println(this.visqm(q, true));

		}
	}

	/**
	 * Visualize the data in the QMatrix.
	 * @param q Q(s,a) matrix
	 * @param vals if true, the value of the best action will be displayed, 
	 * if false the index of best action will be displayed 
	 * @return string visualizing the matrix
	 */
	private String visqm(FinalQMatrix<Double> q, boolean vals){
		String line = "===================================\n";

		int[] dimsizes = q.getDimensionSizes();
		int[] state = new int[]{0,0};
		Double [] actionvals;
		int best;

		if(dimsizes.length != 3)
			System.err.println("only 2 variables supported, not "+dimsizes.length);

		for(int i=0; i<dimsizes[0]; i++){
			state[0] = i;

			for(int j=0; j<dimsizes[1]; j++){
				state[1] = j;
				actionvals = q.getActionValsInState(state);
				best = this.getMaxInd(actionvals);
				if(vals){
					line = line+"\t"+round(actionvals[best],1000);
					//line = line+"\t"+actionvals[best];
				}else{
					line = line+"\t"+best;
				}
			}
			line = line + "\n";
		}
		return line;
	}
	
	private double round(double what, int how){
		int rd = (int)(what*how);
		double d = (double)rd;
		return d/how;
	}

	private String vis(float[][] map){
		String line = "------------------------------------\n";
		for(int i=0; i<map.length; i++){
			for(int j=0; j<map[0].length; j++){
				line = line + "\t "+map[i][j];
			}
			line = line+"\n";
		}
		return line+"\n-------------------------------------";
	}

	private int getMaxInd(Double[] actions){
		int ind = 0;
		for(int i=0; i<actions.length; i++){
			if(actions[ind]<actions[i])
				ind = i;
		}
		return ind;
	}

	private int generateAction(Random r, int numActions){
		return r.nextInt(numActions);
	}

	/**
	 * Move in the environment surrounded by walls. 
	 * 
	 * @param sx x size of map
	 * @param sy y size of map
	 * @param action 0-left, 1-right, 2-up, 3-down
	 * @param current current position on the map
	 * @return new position
	 */
	private int[] makeStep(int sx, int sy, int action, int[] current){
		int[] coords = current.clone();

		if(action==0){ 					// left
			if(current[0] > 0){
				coords[0] = current[0]-1;
			}
		}else if(action==1){ 			// left
			if(current[0] < sx-1){
				coords[0] = current[0]+1;
			}
		}else if(action==2){ 			// up
			if(current[1] < sy-1){
				coords[1] = current[1]+1;
			}
		}else if(action==3){ 			// down
			if(current[1] > 0){
				coords[1] = current[1]-1;
			}
		}else{
			System.err.println("unrecognized action!");
		}
		return coords;
	}

	/**
	 * Build a 2D grid map where the agent receives reward of given value on the selected coordinates. 
	 * @param sx x size of the map 
	 * @param sy y size of the map
	 * @param rewardCoords coordinates of the reward on the map
	 * @param rewardVal value of received reward
	 * @return 2D grid map with values of reward to be received by the agent on the same position 
	 */
	private float[][] simpleRewardMap(int sx, int sy, int[] rewardCoords, float rewardVal){

		float[][] rm = new float[sx][sy];

		for(int i=0; i<rm.length; i++){
			for(int j=0; j<rm[0].length; j++){
				rm[i][j] = 0;
			}
		}
		rm[rewardCoords[0]][rewardCoords[1]] = rewardVal;
		return rm;
	}

}
