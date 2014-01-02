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
	protected boolean better(Double a, Double b) { return a>b;	}
}
