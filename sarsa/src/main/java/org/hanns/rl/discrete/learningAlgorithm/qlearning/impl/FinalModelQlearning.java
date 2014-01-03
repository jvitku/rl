package org.hanns.rl.discrete.learningAlgorithm.qLearning.impl;

import org.hanns.rl.discrete.learningAlgorithm.AbstractFinalRL;
import org.hanns.rl.discrete.learningAlgorithm.LearningConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.config.QLearningConfig;

/**
 * QLearning algorithm over the model with final number of actions and state set.
 * 
 * @author Jaroslav Vitku
 *
 */
public class FinalModelQlearning extends AbstractFinalRL /*implements FinalModelLearningAlgorithm*/{

	private QLearningConfig config;

	/**
	 * Get the number of actions available, array defining how many values
	 * can each particular state can have and set the data structures.s
	 * @param stateSizes array defining how many values can each particular state
	 * variable can have
	 * @param numActions number of actions available to the agent
	 */
	public FinalModelQlearning(int[] stateSizes, int numActions, QLearningConfig config){
		super(stateSizes, numActions);

		this.config = config;
	}

	@Override
	public void performLearningStep(int action, float reward, int[] newState) {
		if(!this.config.getLearningEnabled())
			return;

		if(this.prevState == null)
			this.init(newState);

		double prevVal = q.get(prevState, action);	// we were there and made the action
		Double[] currentActions  = q.getActionValsInState(newState);	// action values available now
		double maxActionVal = currentActions[this.maxInd(currentActions)];	// value of the best available action now

		// compute the learning equation
		double learned = prevVal + this.config.getAlpha()*
				(reward+this.config.getGamma()*maxActionVal-prevVal);

		q.set(prevState, action, learned);	// store the value
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
