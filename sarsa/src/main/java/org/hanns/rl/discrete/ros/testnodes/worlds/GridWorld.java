package org.hanns.rl.discrete.ros.testnodes.worlds;

import java.util.Random;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;

/**
 * Simple grid simulation. The world is represented as a 2D matrix containing reward values.
 * No obstacles are allowed, agent is not allowed to go out of the map.
 * 
 * @author Jaroslav Vitku
 */
public class GridWorld {
	/**
	 * Visualize the data in the QMatrix.
	 * @param q Q(s,a) matrix
	 * @param what if 0, rounded values of best action will be 
	 * displayed for each state, if 1, raw values will be displayed,
	 * if 2, the graphical representation of actions will be shown.
	 * @return string visualizing the matrix
	 */
	public static String visqm(FinalQMatrix<Double> q, int what){
		String w = "";
		if(what==0){
			w = "best-action values for the state:";
		}else if(what==1){
			w = "best action in the state:";
		}else{
			w = "best action in the state| graphically:";
		}
		String line = "==================================="+w+"\n";

		int[] dimsizes = q.getDimensionSizes();
		int[] state = new int[]{0,0};
		Double [] actionvals;
		int best;

		if(dimsizes.length != 3)
			System.err.println("only 2 variables supported, not "+dimsizes.length);

		for(int i=dimsizes[1]-1; i>=0; i--){
			state[1] = i;

			for(int j=0; j<dimsizes[0]; j++){
				state[0] = j;
				actionvals = q.getActionValsInState(state);
				best = GridWorld.getMaxInd(actionvals);
				if(what==0){
					if(best<0){
						line = line+"\t"+best;
					}else
						line = line+"\t"+round(actionvals[best],1000);
				}else if(what==1){
					line = line+"\t"+best;
				}else{
					line = line+"\t"+toAction(best);
				}
			}
			line = line + "\n";
		}
		return line;
	}

	public static String toAction(int action){
		if(action==0){
			return "<";
		}else if(action==1){
			return ">";
		}else if(action==2){
			return "^";
		}else if(action==3){
			return "v";
		}
		return ".";
	}

	public static double round(double what, int how){
		int rd = (int)(what*how);
		double d = (double)rd;
		return d/how;
	}

	/**
	 * Visualize the map, axes are as usual, x is horizontal increasing to the right,
	 * y is vertical increasing towards up. 
	 * @param map array of reward values
	 * @return String representing the map
	 */
	public static String vis(float[][] map){
		String line = "------------------------------------\n";
		for(int i=map[0].length-1; i>=0; i--){
			for(int j=0; j<map.length; j++){
				line = line + "\t "+map[j][i];
			}
			line = line+"\n";
		}
		return line+"\n-------------------------------------";
	}

	public static int getMaxInd(Double[] actions){
		int ind = 0;
		boolean found = false;
		for(int i=0; i<actions.length; i++){
			if(actions[ind]>0)
				found = true;
			if(actions[ind]<actions[i]){
				ind = i;
			}
		}
		if(!found)
			return -1;
		return ind;
	}

	public static int generateAction(Random r, int numActions){
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
	public static int[] makeStep(int sx, int sy, int action, int[] current){
		int[] coords = current.clone();

		if(action==0){ 					// left
			if(current[0] > 0){
				coords[0] = current[0]-1;
			}
		}else if(action==1){ 			// right
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
		}else if(action==-1){			// NOOP
			return coords;
		}else{
			System.err.println("unrecognized action! Action:"+action);
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
	public static float[][] simpleRewardMap(int sx, int sy, int[] rewardCoords, float rewardVal){

		float[][] rm = new float[sx][sy];

		for(int i=0; i<rm.length; i++){
			for(int j=0; j<rm[0].length; j++){
				rm[i][j] = 0;
			}
		}
		if(rewardCoords!=null)
			rm[rewardCoords[0]][rewardCoords[1]] = rewardVal;
		return rm;
	}

	

}
