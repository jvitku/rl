package org.hanns.rl.discrete.q;

import java.util.Random;

import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethod;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.BasicEpsilonGeedyConf;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.BasicConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.EpsilonGreedyDouble;
import org.hanns.rl.discrete.actions.ActionSetInt;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.learningAlgorithm.FinalModelLearningAlgorithm;
import org.hanns.rl.discrete.learningAlgorithm.config.impl.BasicConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.sarsa.FinalModelSarsa;
import org.hanns.rl.discrete.ros.testnodes.worlds.GridWorld;
import org.junit.Test;

import ctu.nengoros.util.SL;
import static org.junit.Assert.fail;


/**
 * The same as tests in the {@link FinalQLearningGreedy}, but here, 
 * but here, the FinalModelQLearning class with a common learning method is tested.
 * 
 * @author Jaroslav Vitku
 *
 */
public class FinalQLearningCommon {
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

		//new FinalModelSarsa(int[] stateSizes, int numActions, LearningConfig config){
		FinalModelLearningAlgorithm ql = new FinalModelSarsa(stateSizes, numActions, config);

		float[][] map = GridWorld.simpleRewardMap(sx, sy, new int[]{7,4}, 1);
		System.out.println("map generated is: \n"+GridWorld.vis(map));

		int[] pos = new int[]{2,2};	// agents position on the map
		int numsteps = 50000;
		int action, prevAction;
		float reward;

		@SuppressWarnings("unchecked")
		FinalQMatrix<Double> q = (FinalQMatrix<Double>)(ql.getMatrix());
		prevAction = GridWorld.generateAction(r, numActions);	// select first action
		
		for(int i=0; i<numsteps; i++){
			pos = GridWorld.makeStep(sx, sy, prevAction, pos);	// move agent
			reward = map[pos[0]][pos[1]];					// read reward
			action = GridWorld.generateAction(r, numActions);	// select the future action
			ql.performLearningStep(prevAction, reward, pos, action);	// learn about it
			prevAction = action;
			if(i%1000==0){
				System.out.println("step "+i);
			}
		}
		System.out.println(GridWorld.visqm(q, 0));
		System.out.println(GridWorld.visqm(q, 1));
		System.out.println(GridWorld.visqm(q, 2));
		System.out.println(GridWorld.vis(map));
		
		System.out.println("Starting the navigation tests now");
		this.navigate(q, 4, sx+sy, map, new int[]{1,1});	// almost corner
		this.navigate(q, 4, sx+sy, map, new int[]{5,5});
		this.navigate(q, 4, sx+sy, map, new int[]{8,5});	// almost corner
		this.navigate(q, 4, sx+sy, map, new int[]{8,1});	// almost corner
		this.navigate(q, 4, sx+sy, map, new int[]{1,5});	// almost corner
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
		ActionSetInt actions= new BasicFinalActionSet(new String[]{
		"<",">","^","v"});
		
		int sx = 10;
		int sy = 7;
		int[] stateSizes = new int[]{sx,sy}; 
		int numActions = actions.getNumOfActions();
		float[][] map = GridWorld.simpleRewardMap(sx, sy, new int[]{7,4}, 1);
		System.out.println("map generated is: \n"+GridWorld.vis(map));
		
		/**
		 * Configure ASM
		 */
		BasicEpsilonGeedyConf econf = new BasicConfig();
		econf.setEpsilon(0.5);	//
		econf.setExplorationEnabled(true);
		ActionSelectionMethod<Double> asm = new EpsilonGreedyDouble(actions,econf);
		
		/**
		 * Configure the learning algorithm
		 */
		BasicConfiguration config = new BasicConfiguration();
		config.setAlpha(0.5);	// learn half of the information
		config.setGamma(0.3);	// more towards immediate reward
		
		FinalModelLearningAlgorithm ql = new FinalModelSarsa(stateSizes, numActions, config);

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
			pos = GridWorld.makeStep(sx, sy, prevAction, pos);	// move agent
			reward = map[pos[0]][pos[1]];					// read reward
			ql.performLearningStep(prevAction, reward, pos, action);// learn about it
			prevAction = action;							// prepare action to be executed
			
			if(i % 1000==0){
				System.out.println("step "+i);
			}
		}
		System.out.println(GridWorld.visqm(q, 0));
		System.out.println(GridWorld.visqm(q, 1));
		System.out.println(GridWorld.visqm(q, 2));
		System.out.println(GridWorld.vis(map));
		
		System.out.println("Starting the navigation tests now");
		this.navigate(q, 4, 5, map, new int[]{5,5}); // not entire map is explored
		this.navigate(q, 4, 5, map, new int[]{7,6});	 
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
		ActionSetInt actions = new BasicFinalActionSet(numActions);
		// use the epsilon-greedy ASM with exploration disabled
		BasicEpsilonGeedyConf econf = new BasicConfig();
		econf.setExplorationEnabled(false);
		ActionSelectionMethod<Double> asm = new EpsilonGreedyDouble(actions, econf);
		
		int action;
		int[] pos = startingPos.clone();
		double reward;
		
		for(int i=0; i<numSteps; i++){
			Double[] actionVals = q.getActionValsInState(pos);			// read action utilities
			action = asm.selectAction(actionVals);						// select the best one
			pos = GridWorld.makeStep(map.length, map[0].length, action, pos);// move agent
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


}
