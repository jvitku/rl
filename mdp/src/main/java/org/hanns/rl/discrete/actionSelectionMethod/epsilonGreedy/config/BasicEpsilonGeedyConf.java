package org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config;

/**
 * Entire configuration of the algorithm.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface BasicEpsilonGeedyConf extends EpsilonGreedyConf {

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
	
	
	

}
