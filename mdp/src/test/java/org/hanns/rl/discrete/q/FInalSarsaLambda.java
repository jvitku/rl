package org.hanns.rl.discrete.q;

import static org.junit.Assert.fail;

import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethod;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.BasicEpsilonGeedyConf;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.BasicConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.EpsilonGreedyDouble;
import org.hanns.rl.discrete.actions.ActionSetInt;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.learningAlgorithm.FinalModelLearningAlgorithm;
import org.hanns.rl.discrete.learningAlgorithm.lambda.impl.NStepQLambdaConfImpl;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.impl.FinalModelQLambda;
import org.hanns.rl.discrete.ros.testnodes.worlds.GridWorld;
import org.junit.Test;

import ctu.nengoros.util.SL;

public class FInalSarsaLambda {

	/**
	 * Significant improvement compared to the one step TD Q-learning verified, 
	 * this is able to find some strategy from about half of map during 500 steps
	 * the one-step TD found 2 state-action pairs in 500 steps.
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
		float[][] map = GridWorld.simpleRewardMap(sx, sy, new int[]{7,4}, 100);
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
		NStepQLambdaConfImpl config = new NStepQLambdaConfImpl(20);
		config.setLambda(0.9);	
		config.setAlpha(0.5);	// learn half of the information
		config.setGamma(0.7);	// more towards immediate reward

		// TODO package structure changed, check this
		FinalModelLearningAlgorithm ql = new FinalModelQLambda(stateSizes, numActions, config);

		/**
		 * Configure the simulation
		 */
		int[] pos = new int[]{2,2};	// agents position on the map
		int numsteps = 1000;
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

			if(i%1000==0){
				System.out.println("step "+i);
			}
			/*
			System.out.println(GridWorld.visqm(q, 0));
			try {
				System.in.read();
			} catch (IOException e) { e.printStackTrace(); }
			*/
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
