package org.hanns.rl.discrete.actionSelectionStrategy.greedy;

import org.hanns.rl.discrete.actionSelectionStrategy.ActionSelectionMethod;
import org.hanns.rl.discrete.actions.ActionSet;

/**
 * Abstract implementation of the Greedy action selection mechanism. 
 * 
 * @author Jaroslav Vitku
 *
 * @param <E> should define some objective defining utility of an action (e.g. Double value)
 */
public abstract class Greedy<E> implements ActionSelectionMethod<E>{

	private ActionSet actions;
	
	public Greedy(ActionSet actions){
		this.actions = actions;
		
	}

	@Override
	public int selectAction(E[] actionValues) {
		if(actionValues.length!=actions.getNumOfActions()){
			System.err.println("ERROR: incorrect size of actionValues array!");
			return -1;
		}
		int ind = 0;
		for(int i=1; i<actionValues.length; i++){
			if(this.better(actionValues[ind], actionValues[i])){
				ind = i;
			}
		}
		return ind;
	}
	
	/**
	 * Implement this in order to use the Greedy algorithm
	 * @param a first parameter
	 * @param b second parameter
	 * @return true if a is a better action (bigger utility value) than b
	 */
	protected abstract boolean better(E a, E b);

	@Override
	public void setActionSet(ActionSet actions) {this.actions = actions; }

	@Override
	public ActionSet getActionSet() { return this.actions; }
}
