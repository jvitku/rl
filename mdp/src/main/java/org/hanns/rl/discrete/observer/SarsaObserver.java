package org.hanns.rl.discrete.observer;

import ctu.nengoros.network.node.observer.Observer;

/**
 * Observer used for observing discrete SARSA algorithms.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface SarsaObserver extends Observer{

	/**
	 * Similar to the 
	 * {@link org.hanns.rl.discrete.learningAlgorithm.LearningAlgorithmInt#performLearningStep(int, float, int[], int)},
	 * but this does not produce any RL action, nor RL-learning. This just 
	 * logs or visualizes agents behavior if conditions are met. 
	 * 
	 * @param prevAction previous action
	 * @param reward reward received from the previous action
	 * @param currentState current state
	 * @param futureAction action selected by the ASM to be executed in the following step
	 */
	public void observe(int prevAction, float reward, int[] currentState, int futureAction);

}
