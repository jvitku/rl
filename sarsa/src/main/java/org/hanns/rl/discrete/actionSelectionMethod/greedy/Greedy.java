package org.hanns.rl.discrete.actionSelectionMethod.greedy;

import java.util.Random;

import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethod;
import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethodConfig;
import org.hanns.rl.discrete.actions.ActionSetInt;

/**
 * Abstract implementation of the Greedy action selection mechanism. 
 * 
 * @author Jaroslav Vitku
 *
 * @param <E> should define some objective defining utility of an action (e.g. Double value)
 */
public abstract class Greedy<E> implements ActionSelectionMethod<E>{

	private boolean wasgreedy;
	private Random r;
	private ActionSetInt actions;

	public Greedy(ActionSetInt actions){
		this.actions = actions;
		r = new Random();
		wasgreedy = false;
	}

	@Override
	public int selectAction(E[] actionValues) {
		if(actionValues.length!=actions.getNumOfActions()){
			System.err.println("ERROR: incorrect size of actionValues array!");
			this.wasgreedy = false;
			return -1;
		}
		// if all actions have equal value, select randomly
		if(this.allEqual(actionValues)){
			this.wasgreedy = false;
			return r.nextInt(actions.getNumOfActions());
		}

		int ind = 0;
		for(int i=1; i<actionValues.length; i++){
			if(this.better(actionValues[i], actionValues[ind])){
				ind = i;
			}
		}
		this.wasgreedy = true;
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
	public void setActionSet(ActionSetInt actions) {this.actions = actions; }

	@Override
	public ActionSetInt getActionSet() { return this.actions; }

	@Override
	public boolean actionWasGreedy() { return this.wasgreedy; }
	
	@Override
	public ActionSelectionMethodConfig getConfig() {
		System.err.println("Greedy ASM: ERROR: I have no config");
		return null;
	}

	@Override
	public void setConfig(ActionSelectionMethodConfig config) {
		System.err.println("Greedy ASM: ERROD: I do not need any config");
	}
	
	@Override
	public void hardReset(boolean randomize) {
		this.wasgreedy = false;
	}

	@Override
	public void softReset(boolean randomize) {
		this.wasgreedy = false;
	}
}
