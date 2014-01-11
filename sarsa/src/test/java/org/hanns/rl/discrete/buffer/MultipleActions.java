package org.hanns.rl.discrete.buffer;

import static org.junit.Assert.fail;

import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethod;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.BasicEpsilonGeedyConf;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.BasicConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.EpsilonGreedyDouble;
import org.hanns.rl.discrete.actions.ActionBufferInt;
import org.hanns.rl.discrete.actions.ActionSet;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.learningAlgorithm.FinalModelLearningAlgorithm;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.config.impl.BasicConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.impl.FinalModelQlearning;
import org.hanns.rl.discrete.ros.testnodes.worlds.GridWorld;
import org.junit.Ignore;
import org.junit.Test;

import ctu.nengoros.util.SL;

/**
 * Test whether the:
 * -one step buffer works OK
 * -two step works bad
 * -two step buffer works with one-step delayed closed-loop 
 * works identically as one step buffer
 * 
 * @author Jaroslav Vitku
 *
 */
public class MultipleActions {

	@Ignore
	@Test
	public void oneStepBuffer(){
		this.oneStepBufferPerformance();
	}

	@Ignore
	@Test
	public void twoStepBufferTest(){
		this.twoStepBufferPerformance();
	}

	//@Ignore
	@Test
	public void twoStepBufferDelayed(){
		twoStepBufferDelayedLoop();
	}

	/**
	 * Return performance of one step buffer on the selected task
	 * @return
	 */
	public int oneStepBufferPerformance(){
		/**
		 * Build the map
		 */
		ActionSet actions= new BasicFinalActionSet(new String[]{
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
		ActionBufferInt a = config.getBuffer();

		FinalModelLearningAlgorithm ql = new FinalModelQlearning(stateSizes, numActions, config);

		/**
		 * Configure the simulation
		 */
		int[] pos = new int[]{2,2};	// agents position on the map
		int numsteps = 70000;
		int action;
		float reward = 0;

		@SuppressWarnings("unchecked")
		FinalQMatrix<Double> q = (FinalQMatrix<Double>)(ql.getMatrix());

		for(int i=0; i<numsteps; i++){

			Double[] vals = q.getActionValsInState(pos);		// read action utilities
			action = asm.selectAction(vals);					// select action

			ql.performLearningStep(a, reward, pos, action);		// learn about it

			pos = GridWorld.makeStep(sx, sy, action, pos);	// move agent
			reward = map[pos[0]][pos[1]];						// read reward

			if(i % 1000==0){
				System.out.println("step "+i);
			}
		}
		System.out.println(GridWorld.visqm(q, 0));
		System.out.println(GridWorld.visqm(q, 1));
		System.out.println(GridWorld.visqm(q, 2));
		System.out.println(GridWorld.vis(map));

		System.out.println("Starting the navigation tests now");
		this.navigate(q, 4, 8, map, new int[]{5,5}, false); // not entire map is explored
		this.navigate(q, 4, 8, map, new int[]{7,6}, false);

		return -1;
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
	private void navigate(FinalQMatrix<Double> q, int numActions,int numSteps, float[][] map, 
			int[] startingPos, boolean shouldFail){
		ActionSet actions = new BasicFinalActionSet(numActions);
		// use the epsilon-greedy ASM with exploration disabled
		//ImportanceBasedConfig econf = new ImportanceBasedConfig();
		BasicEpsilonGeedyConf econf = new BasicConfig();
		econf.setExplorationEnabled(false);
		//econf.setImportance(0.99f);	// still, some randomization is required
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
				if(shouldFail)
					fail();
				return;
			}
		}
		System.out.println("Agent did not reach the reward in predefined "+numSteps+" simulation steps");
		if(!shouldFail){
			fail();
		}

	}


	/**
	 * Performance of two step buffer 
	 * @return
	 */
	public int twoStepBufferPerformance(){
		/**
		 * Build the map
		 */
		ActionSet actions= new BasicFinalActionSet(new String[]{
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
		ActionBufferInt a = config.getBuffer();
		a.setLength(2);

		FinalModelLearningAlgorithm ql = new FinalModelQlearning(stateSizes, numActions, config);

		/**
		 * Configure the simulation
		 */
		int[] pos = new int[]{2,2};	// agents position on the map
		int numsteps = 70000;
		int action;
		float reward = 0;

		@SuppressWarnings("unchecked")
		FinalQMatrix<Double> q = (FinalQMatrix<Double>)(ql.getMatrix());

		for(int i=0; i<numsteps; i++){

			Double[] vals = q.getActionValsInState(pos);		// read action utilities
			action = asm.selectAction(vals);					// select action

			ql.performLearningStep(a, reward, pos, action);		// learn about it

			pos = GridWorld.makeStep(sx, sy, action, pos);	// move agent
			reward = map[pos[0]][pos[1]];						// read reward

			if(i % 1000==0){
				System.out.println("step "+i);
			}
		}
		System.out.println(GridWorld.visqm(q, 0));
		System.out.println(GridWorld.visqm(q, 1));
		System.out.println(GridWorld.visqm(q, 2));
		System.out.println(GridWorld.vis(map));

		System.out.println("Starting the navigation tests now");
		this.navigate(q, 4, 8, map, new int[]{5,5}, true); // not entire map is explored
		this.navigate(q, 4, 8, map, new int[]{7,6}, true);

		return -1;
	}

	// TODO this test does not work!
	/**
	 * Performance of two-step buffer on delayed loop
	 * @return
	 */
	public int twoStepBufferDelayedLoop(){
		/**
		 * Build the map
		 */
		ActionSet actions= new BasicFinalActionSet(new String[]{"<",">","^","v"});

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
		ActionBufferInt a = config.getBuffer();
		a.setLength(2);

		FinalModelLearningAlgorithm ql = new FinalModelQlearning(stateSizes, numActions, config);

		/**
		 * Configure the simulation
		 */
		int[] pos = new int[]{2,2};	// agents position on the map
		int numsteps = 70000;
		int action;
		float reward = 0;

		int current = 0;	// switch between two responses to the agent
		int [][]posBuffer = new int[2][2]; // remember two (past) states
		float [] rewBuffer = new float[2];// remember two (past) rewards
		int[] actionBuff = new int[2];
		int ac;

		@SuppressWarnings("unchecked")
		FinalQMatrix<Double> q = (FinalQMatrix<Double>)(ql.getMatrix());

		for(int i=0; i<numsteps; i++){

			Double[] vals = q.getActionValsInState(pos);		// read action utilities
			action = asm.selectAction(vals);					// select action

			ql.performLearningStep(a, reward, pos, action);		// learn about it

			//System.out.println("Agent before executing step, pos: "+SL.toStr(pos)+" aciton: "+action);
			
			pos = GridWorld.makeStep(sx, sy, action, pos);	// move agent
			reward = map[pos[0]][pos[1]];					// read reward

			posBuffer[current][0] = pos[0];
			posBuffer[current][1] = pos[1];					// store the state
			rewBuffer[current] = reward; 					// read the reward
			actionBuff[current] = action;

			if(current==0)		// buffer values for one step (as the Nengo does)
				current = 1;
			else
				current = 0;

//			System.out.println("Agent after executing step, pos: "+SL.toStr(pos)+
	//				" exctd. aciton: "+action+" rew: "+reward);
			
			//System.out.println("pos: just processed state: "+SL.toStr(pos)+
				//	" will be returned to the agent ["+ posBuffer[current][0]+", "+posBuffer[current][1]+"]");
/*
			try{
				System.in.read();
			}catch(Exception e){}
*/
			pos[0] = posBuffer[current][0];
			pos[1] = posBuffer[current][1];	// read the state
			reward = rewBuffer[current]; 	// read the reward
			ac = actionBuff[current];
			
	//		System.out.println("Sending to the agent this buffered state: "+SL.toStr(pos)+
		//			" action was: "+ac+"rew: "+reward);
			/**/

			if(i % 1000==0){
				System.out.println("step "+i);
			}
		}
		System.out.println(GridWorld.visqm(q, 0));
		System.out.println(GridWorld.visqm(q, 1));
		System.out.println(GridWorld.visqm(q, 2));
		System.out.println(GridWorld.vis(map));

		System.out.println("Starting the navigation tests now");
		this.navigate(q, 4, 8, map, new int[]{5,5}, false); 
		this.navigate(q, 4, 8, map, new int[]{7,6}, false);

		return -1;
	}

}
