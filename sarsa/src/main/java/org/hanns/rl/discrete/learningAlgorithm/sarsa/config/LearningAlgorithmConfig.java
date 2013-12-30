package org.hanns.rl.discrete.learningAlgorithm.sarsa.config;

/**
 * ActionSelectionConfig of learning algorithm.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface LearningAlgorithmConfig {
	
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
	
	/**
	 * Parameter for computing eligibility traces.
	 * @param lambda
	 */
	public void setLambda(double lambda);
	public double getLambda();
	
	/**
	 * Length of eligibility trace to be computed.
	 * @param length
	 */
	public void setEligibilityLength(int length);
	public int getEligibilityLength();

}
