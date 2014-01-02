package org.hanns.rl.discrete.actionSelectionStrategy.greedy;

import java.util.Random;

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

	private Random r;
	private ActionSet actions;

	public Greedy(ActionSet actions){
		this.actions = actions;
		r = new Random();
	}

	@Override
	public int selectAction(E[] actionValues) {
		if(actionValues.length!=actions.getNumOfActions()){
			System.err.println("ERROR: incorrect size of actionValues array!");
			return -1;
		}
		// if all actions have equal value, select randomly
		if(this.allEqual(actionValues)){
			return r.nextInt(actions.getNumOfActions());
		}

		int ind = 0;
		for(int i=1; i<actionValues.length; i++){
			if(this.better(actionValues[i], actionValues[ind])){
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

	/**
	 * Should return true if all actions have the same utility 
	 * @param acitonValues utility values for actions
	 * @return true if all values have equal utility
	 */
	protected abstract boolean allEqual(E[] acitonValues);

	@Override
	public void setActionSet(ActionSet actions) {this.actions = actions; }

	@Override
	public ActionSet getActionSet() { return this.actions; }
}
