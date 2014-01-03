package org.hanns.rl.discrete.learningImprovement;

import org.hanns.rl.common.Resettable;

/**
 * Improvement of the Learning Algorithm.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface LearningAlgorithmImprovement extends Resettable{
	
	/**
	 * Similar to the method 
	 * {@link org.hanns.rl.discrete.learningAlgorithm.LearningAlgorithm#performLearningStep(int, float, int[])}, 
	 * but here, the additional modification of Q(s,a) matrix can be made.
	 * @param action action taken by the agent
	 * @param reward reward received as a result of the action
	 * @param state new state (the previous is stored by the LearningAlgorithmImprovement itself if needed)
	 */
	public void performLearningStep(int action, float reward, int[] state);

}
