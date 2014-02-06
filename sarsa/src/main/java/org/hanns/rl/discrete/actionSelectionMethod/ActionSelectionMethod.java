package org.hanns.rl.discrete.actionSelectionMethod;

import org.hanns.rl.discrete.actions.ActionSetInt;

import ctu.nengoros.network.common.Resettable;

/**
 * Interface for action selection method. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface ActionSelectionMethod<E> extends Resettable{

	/**
	 * Array of values for each action in the current set.
	 * 
	 * @param actionValues array of values for each action
	 * @return index of action from the ActionSetInt that is currently selected
	 */
	public int selectAction(E[] actionValues);
	
	/**
	 * Set the action set to this action selection method. 
	 * @param actions ActionSetInt which contains currently available actions
	 */
	public void setActionSet(ActionSetInt actions);
	
	/**
	 * Get the current set of actions
	 * @return ActionSetInt defining set of currently available actions
	 */
	public ActionSetInt getActionSet();
	
	/**
	 * Return true if the last action selected by the ASM was greedy.
	 * This can be used by e.g. the eligibility traces. 
	 * @return true if the last action was greedy
	 */
	public boolean actionWasGreedy();
	
	/**
	 * Get the class which defines configuration of the method
	 * @return configuration class for this ASM
	 */
	public ActionSelectionMethodConfig getConfig();
	
	public void setConfig(ActionSelectionMethodConfig config);
	
}
