package org.hanns.rl.discrete.learningAlgorithm.qLearning.config;

import org.hanns.rl.discrete.learningAlgorithm.LearningConfiguration;

/**
 * Configuration of the Q-learning algorithm.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface QLearningConfig extends LearningConfiguration{
	
	/**
	 * Learning rate.
	 * 
	 * @param alpha
	 */
	public void setAlpha(double alpha);
	public double getAlpha();
	
	/**
	 * Forgetting rate.
	 * 
	 * @param gamma
	 */
	public void setGamma(double gamma);
	public double getGamma();
}
