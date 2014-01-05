package org.hanns.rl.discrete.learningAlgorithm.qLearning.impl;

import org.hanns.rl.discrete.learningAlgorithm.AbstractFinalRL;
import org.hanns.rl.discrete.learningAlgorithm.LearningConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.config.QLearningConfig;

/**
 * QLearning algorithm over the model with final number of actions and state set.
 * 
 * This is naive version of Q-learning algorithm, for learning, use always the optimal
 * state-action pair (the one with the highest utility ~ greedy selection), no matter
 * which action will be executed (by e.g. epsilon-greedy ASM).
 * 
 * @see <a href="http://www.tu-chemnitz.de/informatik/KI/scripts/ws0910/ml09_7.pdf">Eligibility Traces</a>
 * 
 * @author Jaroslav Vitku
 *
 */
public class FinalModelQlearningNaive extends AbstractFinalRL /*implements FinalModelLearningAlgorithm*/{

	private QLearningConfig config;

	/**
	 * Get the number of actions available, array defining how many values
	 * can each particular state can have and set the data structures.s
	 * @param stateSizes array defining how many values can each particular state
	 * variable can have
	 * @param numActions number of actions available to the agent
	 */
	public FinalModelQlearningNaive(int[] stateSizes, int numActions, QLearningConfig config){
		super(stateSizes, numActions);

		this.config = config;
	}

	/**
	 * Here, the parameter futureAction is ignored and the learning computes with the 
	 * optimal policy (best action in the current state), no matter which action 
	 * has been chosen for execution.
	 */
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

	//@Override
	public QLearningConfig getConfig() { return this.config; }

}
