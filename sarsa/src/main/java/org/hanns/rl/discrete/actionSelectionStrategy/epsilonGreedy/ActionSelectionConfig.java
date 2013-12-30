package org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy;

/**
 * Entire configuration of the algorithm.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface ActionSelectionConfig {

	/**
	 * <p>This ensures balance between exploitation of learned knowledge
	 * and exploitation (learning new things).</p>
	 * 
	 * <p>The parameter defines the minimum probability of choosing random
	 * action.  By means of correct use of this parameter,
	 * the algorithm will not over-learn over time.</p>
	 * 
	 * @param min minimum probability of randomizing.
	 */
	public void setMinEpsilon(double min);
	public double getMinEpsilon(double min);

}
