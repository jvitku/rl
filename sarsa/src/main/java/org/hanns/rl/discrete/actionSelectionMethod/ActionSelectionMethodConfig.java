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

}
