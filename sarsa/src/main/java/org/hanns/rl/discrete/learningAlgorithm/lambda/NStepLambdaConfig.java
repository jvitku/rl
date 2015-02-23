package org.hanns.rl.discrete.learningAlgorithm.lambda;

import org.hanns.rl.discrete.learningAlgorithm.config.LearningConfig;

/**
 * 
 * This is configuration of the backward SARSA(lambda) eligibility trace for state-action pairs. 
 * This algorithm is an improvement of Q-learning algorithm by means of learning multiple steps
 * backwards.
 * For more information, visit e.g. @see <a href="http://www.tu-chemnitz.de/informatik/KI/scripts/ws0910/ml09_7.pdf">
 * Eligibility Traces</a>.
 *  
 * @author Jaroslav Vitku
 *
 */
public interface NStepLambdaConfig extends LearningConfig{
	
	/**
	 * Set the length of eligibility trace, how many state-action pairs to update each step
	 * @param length number of remembered states visited in the past
	 */
	public void setEligibilityLength(int length);

	/**
	 * Get he length of eligibility trace.
	 * @return how many state-action pairs is updated during each step
	 */
	public int getEligibilityLength();


	/**
	 * Set the Lambda parameter of eligibility trace. Lambda defines decay 
	 * with which the current state-action pair projects into the past.
	 * The bigger Lambda, the bigger decay. Bigger decay means less information 
	 * is projected into the past, but also better stability of learning convergence.    
	 * @param lambda set decay of projection information into the past. Choose values between 0 and 1. 
	 */
	public void setLambda(double lambda);
	
	/**
	 * Get the lambda parameter, for more information, see {@link #setLambda(double)}
	 * @return value of lambda
	 */
	public double getLambda();
	
	/**
	 * Return the array of pre-computed decays for particular states stored in
	 * the eligibility trace. 
	 * @return array of double values computed according to: 
	 * decay(t) = power(gamma*lambda,t)
	 */
	public double[] getdecays();
}


