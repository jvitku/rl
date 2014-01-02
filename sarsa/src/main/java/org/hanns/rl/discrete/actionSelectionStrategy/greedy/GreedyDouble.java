package org.hanns.rl.discrete.actionSelectionStrategy.greedy;

import org.hanns.rl.discrete.actions.ActionSet;

/**
 * Greedy action selection method (ASM) which operates over Double values.
 * 
 * @author Jaroslav Vitku
 *
 */
public class GreedyDouble extends Greedy<Double>{

	public GreedyDouble(ActionSet actions) {
		super(actions);
	}

	@Override
	protected boolean better(Double a, Double b) { 
		return a.doubleValue()>b.doubleValue();
	}

	@Override
	protected boolean allEqual(Double[] actionValues) {
		for(int i=1; i<actionValues.length; i++){
			if(actionValues[0].doubleValue() != actionValues[i].doubleValue())
				return false;
		}
		return true;
	}
}
