package org.hanns.rl.discrete.learningAlgorithm.sarsa;

import org.hanns.rl.discrete.learningAlgorithm.AbstractFinalRL;
import org.hanns.rl.discrete.learningAlgorithm.config.LearningConfig;

/**
 * SARSA algorithm over the model with final number of actions and state set.
 *  
 * @see <a href="http://www.tu-chemnitz.de/informatik/KI/scripts/ws0910/ml09_7.pdf">Eligibility Traces</a>
 * 
 * @author Jaroslav Vitku
 *
 */
public class FinalModelSarsa extends AbstractFinalRL /*implements FinalModelLearningAlgorithm*/{

	/**
	 * Get the number of actions available, array defining how many values
	 * can each particular state can have and set the data structures.s
	 * @param stateSizes array defining how many values can each particular state
	 * variable can have
	 * @param numActions number of actions available to the agent
	 */
	public FinalModelSarsa(int[] stateSizes, int numActions, LearningConfig config) {
		super(stateSizes, numActions, config);
	}

	/**
	 * SARSA updates knowledge in the new state according to the utility value 
	 * of an action which will be executed in the following step.  
	 */
	@Override
	protected double getNewMaxActionVal(Double[] newActions, int selectedAction) {
		return newActions[selectedAction];
	}

}
