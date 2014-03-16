package org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.EpsilonGreedy;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.BasicEpsilonGeedyConf;
import org.hanns.rl.discrete.actions.ActionSetInt;

/**
 * Epsilon greedy for float utility values.
 * 
 * @author Jaroslav Vitku
 *
 */
public class EpsilonGreedyFloat extends EpsilonGreedy<Float>{

	public EpsilonGreedyFloat(ActionSetInt actions, BasicEpsilonGeedyConf config) {
		super(actions, config);
	}

	@Override
	protected boolean better(Float a, Float b) { 
		return a.doubleValue()>b.doubleValue();
	}

	@Override
	protected boolean allEqual(Float[] actionValues) {

		for(int i=1; i<actionValues.length; i++){
			if(actionValues[0].doubleValue() != actionValues[i].doubleValue()){
				return false;
			}
		}
		return true;
	}
}
