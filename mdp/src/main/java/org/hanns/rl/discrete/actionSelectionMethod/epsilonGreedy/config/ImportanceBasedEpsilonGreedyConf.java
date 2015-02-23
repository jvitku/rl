package org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config;

/**
 * The epsilon-greedy AMS where the current epsilon is determined by the importance
 * of selected action. That means: low importance -> big exploration.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface ImportanceBasedEpsilonGreedyConf extends EpsilonGreedyConf {
	
	/**
	 * Even in case of maximum importance, small randomization should
	 * occur (e.g. stuck in local extreme).
	 * @param min minimum value of epsilon 
	 */
	public void setMinEpsilon(double min);
	
	/**
	 * Return the current value of minimum epsilon 
	 * @return minimum value of the epsilon (probability of randomization) 
	 */
	public double getMinEpsilon();

}
