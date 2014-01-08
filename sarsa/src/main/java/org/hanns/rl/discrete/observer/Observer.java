package org.hanns.rl.discrete.observer;

import org.hanns.rl.common.Resettable;

/**
 * The observer observes agents (nodes) behavior and creates some statistics
 * about it (e.g. to provide the prosperity value).
 *  
 * @author Jaroslav Vitku
 *
 */
public interface Observer extends Resettable{
	
	/**
	 * Similar to the 
	 * {@link org.hanns.rl.discrete.learningAlgorithm.LearningAlgorithm#performLearningStep(int, float, int[], int)},
	 * but this does not produce any RL action, nor RL-learning. This just 
	 * logs agents behavior. 
	 * 
	 * @param prevAction previous action
	 * @param reward reward received from the previous action
	 * @param currentState current state
	 * @param futureAction action selected by the ASM to be executed in the following step
	 */
	public void observe(int prevAction, float reward, int[] currentState, int futureAction);
	
	/**
	 * Value of successfulness of the agent (node). This can be computed
	 * from the data logged by the method {@link #observe(int, float, int[], int)}.
	 * 
	 * Possible candidates are: measure of coverage of the available state-space,
	 * average reward received etc. 
	 * 
	 * @return value from interval [0,1] determining how successful the algorithm is 
	 */
	public float getProsperity();

}
