package org.hanns.rl.discrete.learningAlgorithm.qLearning.impl;

import org.hanns.rl.discrete.learningAlgorithm.AbstractFinalRL;
import org.hanns.rl.discrete.learningAlgorithm.LearningConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.config.QLearningConfig;

/**
 * QLearning algorithm over the model with final number of actions and state set.
 * 
 * Compared to classical definition of the Q-learning algorithm, this 
 * computes the equation with the new state-action pair based on (the current state and)
 * the greedy-selected new action. The action selected by the ASM to be executed 
 * (may or may not be greedy - optimal) is ignored.  
 * 
 * @author Jaroslav Vitku
 *
 */
public class FinalModelQlearningGreedy extends AbstractFinalRL /*implements FinalModelLearningAlgorithm*/{

	private QLearningConfig config;

	/**
	 * Get the number of actions available, array defining how many values
	 * can each particular state can have and set the data structures.s
	 * @param stateSizes array defining how many values can each particular state
	 * variable can have
	 * @param numActions number of actions available to the agent
	 */
	public FinalModelQlearningGreedy(int[] stateSizes, int numActions, QLearningConfig config){
		super(stateSizes, numActions);

		this.config = config;
	}

	@Override
	public void performLearningStep(int prevAction, float reward, int[] newState, int newAction) {
		if(!this.config.getLearningEnabled())
			return;

		if(this.prevState == null)
			this.init(newState);

		// we were there and made the action
		double prevVal = q.get(prevState, prevAction);	
		// action values available now
		Double[] newActions  = q.getActionValsInState(newState);	
		// value of the best available action now
		double maxNewActionVal = newActions[this.maxInd(newActions)];	

		// compute the learning equation
		double learned = prevVal + this.config.getAlpha()*
				(reward+this.config.getGamma()*maxNewActionVal-prevVal);

		q.set(prevState, prevAction, learned);	// store the value
		prevState = newState.clone();		// update last state and action
	}


	@Override
	public void setConfig(LearningConfiguration config) {
		if(!(config instanceof QLearningConfig))
			System.err.println("FinalModelQLearning: The class QLearningCongig expected");
		this.config = (QLearningConfig) config;
	}

	@Override
	public LearningConfiguration getConfig() { return this.config; }

}
