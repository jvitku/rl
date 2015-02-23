package org.hanns.rl.discrete.actionSelectionMethod;

/**
 * Configuration of general action selection algorithm. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface ActionSelectionMethodConfig {
	
	/**
	 * Whether the agent is allowed to use any of exploration techniques. 
	 * @param enable if disabled, the agent will probably use greedy algorithm 
	 */
	public void setExplorationEnabled(boolean enable);
	
	/**
	 * @return true if the exploration is enabled
	 */
	public boolean getExplorationEnabled();

	/**
	 * This is similar method to the HannsNode, the more important
	 * the solution from the algorithm is, the less
	 * exploration (randomization) should occur here. 
	 * @param importance value between [0,1] - 1 means no exploration
	 */
	public void setImportance(float importance);
	
	/**
	 * Get the current value of action importance
	 * @return returns the current value of the importance
	 */
	public float getImportance();
	
	
	/**
	 * Call this method if some parameter was changed. The other 
	 * parameters can be recomputed in this method
	 * 
	 */
	public void fireParamChanged();
}
