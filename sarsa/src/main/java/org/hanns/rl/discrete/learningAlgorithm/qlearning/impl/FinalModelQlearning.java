package org.hanns.rl.discrete.learningAlgorithm.qLearning.impl;

import org.hanns.rl.discrete.actions.ActionBufferInt;
import org.hanns.rl.discrete.learningAlgorithm.AbstractFinalRL;
import org.hanns.rl.discrete.learningAlgorithm.LearningConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.config.QLearningConfig;

/**
 * QLearning algorithm over the model with final number of actions and state set.
 * 
 * This follows the equations from books, the Q(s",a") is based on the current state
 * and the action selected by the current ASM (action selection method).   
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

	/**
	 * Here, the parameter futureAction is ignored and the learning computes with the 
	 * optimal policy (best action in the current state), no matter which action 
	 * has been chosen for execution.
	 */
	@Override
	public void performLearningStep(ActionBufferInt prevActions, float reward, int[] newState, int newAction) {
		if(!this.config.getLearningEnabled())
			return;

		if(this.prevState == null)
			this.init(newState);
		
		if(prevActions.isEmpty())
			prevActions.push(DEF_FIRST_ACT);

		// we were there and made the action
		double prevVal = q.get(prevState, prevActions.read());	
		// action values available now
		Double[] newActions  = q.getActionValsInState(newState);	
		// value of the best available action now
		double newActionVal = newActions[newAction];	

		// compute the learning equation
		double learned = prevVal + this.config.getAlpha()*
				(reward+this.config.getGamma()*newActionVal-prevVal);

		q.set(prevState, prevActions.read(), learned);	// store the value
		
		prevState = newState.clone();		// update last state and action
		prevActions.push(newAction);		// remember what is being executed now
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
