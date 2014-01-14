package org.hanns.rl.discrete.observer;

import org.hanns.rl.common.Resettable;

/**
 * The observer observes agents (nodes) behavior and creates some statistics
 * about it (e.g. to provide the prosperity value). 
 * 
 * Observer can be also any visualization algorithm.
 *  
 * @author Jaroslav Vitku
 *
 */
public interface Observer extends Resettable {
	
	/**
	 * Similar to the 
	 * {@link org.hanns.rl.discrete.learningAlgorithm.LearningAlgorithm#performLearningStep(int, float, int[], int)},
	 * but this does not produce any RL action, nor RL-learning. This just 
	 * logs or visualizes agents behavior if conditions are met. 
	 * 
	 * @param prevAction previous action
	 * @param reward reward received from the previous action
	 * @param currentState current state
	 * @param futureAction action selected by the ASM to be executed in the following step
	 */
	public void observe(int prevAction, float reward, int[] currentState, int futureAction);
	
	/**
	 * Turn on/off the visualization (logging)
	 * @param visualize true if the node should visualize
	 */
	public void setShouldVis(boolean visualize);
	
	/**
	 * @return true if the visualization (console logging) is turned on
	 */
	public boolean getShouldVis();
	
	/**
	 * Set how often (in the algorithm steps) the visualization should occur
	 * @param period How often to update visualization, 1 means every simulation step, 
	 * -1 means no visualization, 10 means visualization each 10 steps
	 */
	public void setVisPeriod(int period);
	
	/**
	 * @see #setVisPeriod(int)
	 * @return how often the visualization occurs
	 */
	public int getVisPeriod();
	
	/**
	 * Return the name of the Observer, the name should somehow
	 * reflect the observers purpose.
	 * @return name of the observer
	 */
	public String getName();
}

