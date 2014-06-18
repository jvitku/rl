package org.hanns.rl.discrete.actions;

import org.hanns.rl.common.exceptions.FinalParamException;

/**
 * List of actions that can be undertaken by the action selection algorithm.
 * Actions are accessed by their indexes. The index -1 means no action. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface ActionSetInt {
	
	/**
	 * Set number of actions. Note that this parameter may be final for some algorithms.
	 * @param actions number of actions
	 * @throws FinalParamException if tried to change the final parameter
	 */
	public void setNoActions(int actions) throws FinalParamException;
	
	public int getNumOfActions();
	
	/**
	 * Each action can have own name.
	 * @param index number of action to setup
	 * @param label new name for the action
	 */
	public void setActionLabel(int index, String label);
	
	/**
	 * Get array of action labels (indexed in the same manned as actions)
	 * @return array of action names
	 */
	public String[] getActionLabels();

	/**
	 * Get name for action with the selected index.
	 * @param index index of the action in the ActionSetInt
	 * @return name of the action, null if the name not set
	 */
	public String getActionName(int index);
}
