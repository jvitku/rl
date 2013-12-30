package org.hanns.rl.discrete.sarsa.config;

/**
 * Entire configuration of the algorithm.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface Configuration {

	/*
	 * Configure parameters of the learning algorithm.
	 */
	public void setAlpha(double alpha);
	public double getAlpha();
	
	public void setGamma(double gamma);
	public double getGamma();
	
	public void setMinEpsilon(double min);
	public double getMinEpsilon(double min);
	
	public void setEligibilityLength(int length);
	public int getEligibilityLength();
	
}
