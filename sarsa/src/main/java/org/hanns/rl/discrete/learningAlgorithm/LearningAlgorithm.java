package org.hanns.rl.discrete.learningAlgorithm;

/**
 * Interface for the learning algorithm. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface LearningAlgorithm {
	
	/**
	 * Retrieve the action performed, reward obtained and observation of the current
	 * world state. The algorithm remembers 
	 *   
	 * @param action index of action that was performed
	 * @param reward value of reward received from the environment (architecture) 
	 * @param state state observed on inputs
	 */
	public void performLearningStep(int action, float reward, int[] state);	
	
	// TODO: what is needed? reward, previous step, current step, previous action??

}

