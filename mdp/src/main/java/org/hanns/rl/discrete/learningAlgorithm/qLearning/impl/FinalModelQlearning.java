package org.hanns.rl.discrete.learningAlgorithm.qLearning.impl;

import org.hanns.rl.discrete.learningAlgorithm.AbstractFinalRL;
import org.hanns.rl.discrete.learningAlgorithm.config.LearningConfig;

/**
 * QLearning algorithm over the model with final number of actions and state set.
 * 
 * The Q-learning algorithm learns always the best action in the new state.  
 * 
 * @see <a href="http://www.tu-chemnitz.de/informatik/KI/scripts/ws0910/ml09_7.pdf">Eligibility Traces</a>
 * 
 * @author Jaroslav Vitku
 *
 */
public class FinalModelQlearning extends AbstractFinalRL{

	public FinalModelQlearning(int[] stateSizes, int numActions, LearningConfig config) {
		super(stateSizes, numActions, config);
	}

	@Override
	protected double getNewMaxActionVal(Double[] newActions, int selectedAction) {
		return newActions[this.maxInd(newActions)];
	}
}
