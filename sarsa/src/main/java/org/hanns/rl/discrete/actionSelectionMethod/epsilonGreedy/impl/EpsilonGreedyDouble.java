package org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.EpsilonGreedy;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.EpsilonGreedyConfig;
import org.hanns.rl.discrete.actions.ActionSet;

/**
 * Epsilon greedy for double utility values.
 * 
 * @author Jaroslav Vitku
 *
 */
public class EpsilonGreedyDouble extends EpsilonGreedy<Double>{

	public EpsilonGreedyDouble(ActionSet actions, EpsilonGreedyConfig config) {
		super(actions, config);
	}

	@Override
	protected boolean better(Double a, Double b) { 
		return a.doubleValue()>b.doubleValue();
	}

	@Override
	protected boolean allEqual(Double[] actionValues) {

		for(int i=1; i<actionValues.length; i++){
			if(actionValues[0].doubleValue() != actionValues[i].doubleValue()){
				return false;
			}
		}
		return true;
	}
}
