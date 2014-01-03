package org.hanns.rl.discrete.learningImprovement;

/**
 * This implements backward SARSA(lambda) eligibility trace for state-action pairs, 
 * for more information, visit e.g. @see <a href="http://www.tu-chemnitz.de/informatik/KI/scripts/ws0910/ml09_7.pdf">
 * Eligibility Traces</a>.
 *   
 *  
 * @author Jaroslav Vitku
 *
 */
public interface NStepEligibilityTrace {
	
	/**
	 * Set the length of eligibility trace, how many state-action pairs to update each step
	 * @param n number of states visited in the past
	 */
	public void setN(int n);
	
	/**
	 * Get he length of eligibility trace.
	 * @return how many state-action pairs is updated during each step
	 */
	public int getN();

	/**
	 * Set the Lambda parameter of eligibility trace. Lambda defines decay 
	 * with which the current state-action pair projects into the past.
	 * The bigger Lambda, the bigger decay. Bigger decay means less information 
	 * is projected into the past, but also better stability of learning convergence.    
	 * @param lambda set decay of projection information into the past. Choose values between 0 and 1. 
	 */
	public void setLamda(double lambda);
	
	/**
	 * Get the lambda parameter, for more information, see {@link #setLamda(double)}
	 * @return value of lambda
	 */
	public double getLabda();
}
