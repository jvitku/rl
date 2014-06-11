package org.hanns.rl.discrete.actions.impl;

import org.hanns.rl.discrete.actions.ActionSet;
import org.hanns.rl.common.exceptions.FinalParamException;

/**
 * Basic action set with the final number of actions.
 * 
 * @author Jaroslav Vitku
 */
public class BasicFinalActionSet extends ActionSet {

	private final int numActions;
	
	private final String[] names;
	
	public BasicFinalActionSet(int numActions){
		this.numActions = numActions;
		this.names = new String[numActions];
	}
	
	public BasicFinalActionSet(String[] labels){
		this.names = labels;
		this.numActions = labels.length;
	}
	
	@Override
	public void setNoActions(int actions) throws FinalParamException {
		throw new FinalParamException("BasicFinalActionSet does not allow changing the number of actions!");
	}

	@Override
	public int getNumOfActions() { return this.numActions;	}


	@Override
	public String[] getActionLabels() { return this.names; }

	@Override
	public void setActionLabel(int actionNo, String label) {
		this.names[actionNo] = label;
	}

	@Override
	public String getActionName(int no) { return this.names[no]; }
}
