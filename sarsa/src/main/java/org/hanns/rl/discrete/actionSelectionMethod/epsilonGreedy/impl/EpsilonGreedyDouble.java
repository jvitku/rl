package org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.EpsilonGreedy;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.BasicEpsilonGeedyConf;
import org.hanns.rl.discrete.actions.ActionSetInt;

/**
 * Epsilon greedy for double utility values.
 * 
 * @author Jaroslav Vitku
 *
 */
public class EpsilonGreedyDouble extends EpsilonGreedy<Double>{

	public EpsilonGreedyDouble(ActionSetInt actions, BasicEpsilonGeedyConf config) {
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
