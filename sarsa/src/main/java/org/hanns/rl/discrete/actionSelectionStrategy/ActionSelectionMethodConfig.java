package org.hanns.rl.discrete.actionSelectionStrategy;

/**
 * Configuration of general action selection algorithm. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface ActionSelectionMethodConfig {
	
	public void setExplorationEnabled(boolean enable);
	
	public boolean getExplorationEnabled();

}
