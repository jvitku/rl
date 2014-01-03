package org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.config;

import org.hanns.rl.discrete.actionSelectionStrategy.ActionSelectionMethodConfig;

/**
 * Entire configuration of the algorithm.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface EpsilonGreedyConfig extends ActionSelectionMethodConfig {

	/**
	 * <p>This ensures balance between exploitation of learned knowledge
	 * and exploitation (learning new things).</p>
	 * 
	 * <p>The parameter defines the probability of choosing random
	 * action with uniform distribution.  By means of correct use of this parameter,
	 * the algorithm will not over-learn over time.</p>
	 * 
	 * @param value probability of selecting the action randomly.
	 */
	public void setEpsilon(double value);
	
	/**
	 * Get the value of epsilon parameter
	 * @return current value of the parameter
	 */
	public double getEpsilon();
	
	/**
	 * For the future use in some adaptive epsilon value
	 * @param min
	 */
	public void setMinEpsilon(double min);
	public double getMinEpsilon(double min);

}
