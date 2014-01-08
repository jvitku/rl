package org.hanns.rl.discrete.visualizaiton;

import org.hanns.rl.common.Resettable;

/**
 * Represents visualization utility. Typically, the visualization of data should not 
 * occur each time step, since it is computationally expensive. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface Visualizer extends Resettable {
	
	
	/**
	 * This is identical to the {@link org.hanns.rl.discrete.observer.Observer#observe(int, float, int[], int)}
	 * or the {@link org.hanns.rl.discrete.learningAlgorithm.LearningAlgorithm#performLearningStep(int, float, int[], int)}, 
	 * but here, the visualization step can occur if some conditions are met.  
	 * @param prevAction action executed from the last state
	 * @param reward reward received after visiting the current state
	 * @param currentState integer array defining the current coordinates in the state-space
	 * @param futureAction action selected to be executed by the ASM
	 */
	public void performStep(int prevAction, float reward, int[] currentState, int futureAction);

	/**
	 * Set how often to update the visualization.
	 * @param period How often to update visualization, 1 means every simulation step, 
	 * -1 means no visualization
	 */
	public void setVisPeriod(int period);
	
	/**
	 * How often the visualization is updated.
	 * @return period of visualization update
	 */
	public int getVisPeriod();
	
	/**
	 * How many details to visualize
	 * @param details the higher number, the more details, and 0 should mean totally silent
	 */
	public void setVisDetails(int details);
	
	/**
	 * Return the number of details to be visualized
	 * @return 0 means no visualization update
	 */
	public int getVisDetails();
	

}
