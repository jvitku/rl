package org.hanns.rl.discrete.actionSelectionMethod.greedy;

import org.hanns.rl.discrete.actions.ActionSetInt;

/**
 * Greedy action selection method (ASM) which operates over Float values.
 * 
 * @author Jaroslav Vitku
 *
 */
public class GreedyFloat extends Greedy<Float>{

	public GreedyFloat(ActionSetInt actions) {
		super(actions);
	}

	@Override
	protected boolean better(Float a, Float b) { 
		return a.doubleValue() > b.doubleValue();
	}

	@Override
	protected boolean allEqual(Float[] actionValues) {
		for(int i=1; i<actionValues.length; i++){
			if(actionValues[0].doubleValue() != actionValues[i].doubleValue())
				return false;
		}
		return true;
	}
}
