package org.hanns.rl.discrete.q;

import java.util.Random;

import org.hanns.rl.discrete.actionSelectionStrategy.ActionSelectionMethod;
import org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.config.EpsilonGreedyConfig;
import org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.config.impl.BasicConfig;
import org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.impl.EpsilonGreedyDouble;
import org.hanns.rl.discrete.actions.ActionSet;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.learningAlgorithm.FinalModelLearningAlgorithm;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.config.impl.BasicConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.impl.FinalModelQlearningGreedy;
import org.junit.Test;

import ctu.nengoros.util.SL;

import static org.junit.Assert.fail;


/**
 * Tests the FinalModelQLearning class.
 * 
 * @author Jaroslav Vitku
 *
 */
public class FinalQLearning {
	/**
	 * Test a simple mobile agent learning navigation in a simple grid environment:
	 * <ul>
	 * <li>Agent can navigate by means of four actions: [0,1,2,3] ~ left, right, up, down</li>
	 * <li>Environment is represented by a reward map: 2D array with values of reward</li>
	 * <li>If the agent steps on a tale with nonzero reward, reward is received</li>
	 * <li>The agent should learn how to navigate towards the reward</li>
	 * <li>Action selection is pure random</li>
	 *  </ul>
	 */
	@Test
	public void basicQLearning(){
		
		Random r = new Random();

		int sx = 10;
		int sy = 7;
		int[] stateSizes = new int[]{sx,sy}; 
		int numActions = 4;		// left, right, up, down

		BasicConfiguration config = new BasicConfiguration();
		config.setAlpha(0.5);	// learn half of the information
		config.setGamma(0.3);	// more towards immediate reward

		//new FinalModelQlearningGreedy(int[] stateSizes, int numActions, QLearningConfig config){
		FinalModelLearningAlgorithm ql = new FinalModelQlearningGreedy(stateSizes, numActions, config);

		float[][] map = this.simpleRewardMap(sx, sy, new int[]{7,4}, 1);
		System.out.println("map generated is: \n"+this.vis(map));

		int[] pos = new int[]{2,2};	// agents position on the map
		int numsteps = 50000;
		int action, prevAction;
		float reward;

		@SuppressWarnings("unchecked")
		FinalQMatrix<Double> q = (FinalQMatrix<Double>)(ql.getMatrix());
		prevAction = this.generateAction(r, numActions);	// select first action
		
		for(int i=0; i<numsteps; i++){
			pos = this.makeStep(sx, sy, prevAction, pos);	// move agent
			reward = map[pos[0]][pos[1]];					// read reward
			action = this.generateAction(r, numActions);	// select the future action
			ql.performLearningStep(prevAction, reward, pos, action);	// learn about it
			prevAction = action;
			if(i%1000==0){
				System.out.println("step "+i);
			}
		}
		System.out.println(this.visqm(q, 0));
		System.out.println(this.visqm(q, 1));
		System.out.println(this.visqm(q, 2));
		System.out.println(this.vis(map));
		
		System.out.println("Starting the navigation tests now");
		this.navigate(q, 4, sx+sy, map, new int[]{0,0});	// corner
		this.navigate(q, 4, sx+sy, map, new int[]{5,5});
		this.navigate(q, 4, sx+sy, map, new int[]{9,6});	// corner
		this.navigate(q, 4, sx+sy, map, new int[]{9,0});	// corner
		this.navigate(q, 4, sx+sy, map, new int[]{0,6});	// corner
		this.navigate(q, 4, sx+sy, map, new int[]{7,4});	// reward pos.
	}
	
	/**
	 * Action selection method is Epsilon-greedy.
	 */
	@Test
	public void asm(){

		/**
		 * Build the map
		 */
		ActionSet actions= new BasicFinalActionSet(new String[]{
		"<",">","^","v"});
		
		int sx = 10;
		int sy = 7;
		int[] stateSizes = new int[]{sx,sy}; 
		int numActions = actions.getNumOfActions();
		float[][] map = this.simpleRewardMap(sx, sy, new int[]{7,4}, 1);
		System.out.println("map generated is: \n"+this.vis(map));
		
		/**
		 * Configure ASM
		 */
		EpsilonGreedyConfig econf = new BasicConfig();
		econf.setEpsilon(0.5);	//
		econf.setExplorationEnabled(true);
		ActionSelectionMethod<Double> asm = new EpsilonGreedyDouble(actions,econf);
		
		/**
		 * Configure the learning algorithm
		 */
		BasicConfiguration config = new BasicConfiguration();
		config.setAlpha(0.5);	// learn half of the information
		config.setGamma(0.3);	// more towards immediate reward
		
		FinalModelLearningAlgorithm ql = new FinalModelQlearningGreedy(stateSizes, numActions, config);

		/**
		 * Configure the simulation
		 */
		int[] pos = new int[]{2,2};	// agents position on the map
		int numsteps = 50000;
		int action, prevAction;
		float reward;

		@SuppressWarnings("unchecked")
		FinalQMatrix<Double> q = (FinalQMatrix<Double>)(ql.getMatrix());
		prevAction = asm.selectAction(q.getActionValsInState(pos));				// select action
		
		for(int i=0; i<numsteps; i++){
			Double[] vals = q.getActionValsInState(pos);	// read action utilities
			action = asm.selectAction(vals);				// select action 
			pos = this.makeStep(sx, sy, prevAction, pos);	// move agent
			reward = map[pos[0]][pos[1]];					// read reward
			ql.performLearningStep(prevAction, reward, pos, action);// learn about it
			prevAction = action;							// prepare action to be executed
			
			if(i%1000==0){
				System.out.println("step "+i);
			}
		}
		System.out.println(this.visqm(q, 0));
		System.out.println(this.visqm(q, 1));
		System.out.println(this.visqm(q, 2));
		System.out.println(this.vis(map));
		
		System.out.println("Starting the navigation tests now");
		this.navigate(q, 4, 4, map, new int[]{5,5}); // not entire map is explored
		this.navigate(q, 4, 4, map, new int[]{7,6});	 
	}
	
	/**
	 * Here is testing the learned knowledge, the agent should be able
	 * to reach the reward in a predefined number of steps if learning
	 * worked well. This should be tested from several random positions on the map.
	 * @param q Q(s,a) matrix
	 * @param numActions number of actions the agent is capable of (=4)
	 * @param numSteps maximum number of simulation steps allowed before failing the test 
	 * @param map map of the environment containing the reinforcement  
	 * @param startingPos starting position on the map
	 */
	private void navigate(FinalQMatrix<Double> q, int numActions,int numSteps, float[][] map, int[] startingPos){
		ActionSet actions = new BasicFinalActionSet(numActions);
		// use the epsilon-greedy ASM with exploration disabled
		EpsilonGreedyConfig econf = new BasicConfig();
		econf.setExplorationEnabled(false);
		ActionSelectionMethod<Double> asm = new EpsilonGreedyDouble(actions, econf);
		
		int action;
		int[] pos = startingPos.clone();
		double reward;
		
		for(int i=0; i<numSteps; i++){
			Double[] actionVals = q.getActionValsInState(pos);			// read action utilities
			action = asm.selectAction(actionVals);						// select the best one
			pos = this.makeStep(map.length, map[0].length, action, pos);// move agent
			System.out.println("Selected action is: "+action+", my position is now: "+SL.toStr(pos));
			reward = map[pos[0]][pos[1]];
			if(reward>0.0){
				System.out.println("GOAL, agent reached the reward of size "+reward+" in "+i+"steps");
				return;
			}
		}
		System.out.println("Agent did not reach the reward in predefined "+numSteps+" simulation steps");
		fail();
	}

	/**
	 * Visualize the data in the QMatrix.
	 * @param q Q(s,a) matrix
	 * @param vals if true, the value of the best action will be displayed, 
	 * if false the index of best action will be displayed 
	 * @return string visualizing the matrix
	 */
	private String visqm(FinalQMatrix<Double> q, int what){
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
				best = this.getMaxInd(actionvals);
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

	private String toAction(int action){
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

	private double round(double what, int how){
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
	private String vis(float[][] map){
		String line = "------------------------------------\n";
		for(int i=map[0].length-1; i>=0; i--){
			for(int j=0; j<map.length; j++){
				line = line + "\t "+map[j][i];
			}
			line = line+"\n";
		}
		return line+"\n-------------------------------------";
	}

	private int getMaxInd(Double[] actions){
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
