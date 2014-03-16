package org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.ImportanceEpsilonGreedy;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.hanns.rl.discrete.actions.ActionSetInt;

/**
 * Importance-based Epsilon greedy for float utility values.
 * 
 * @author Jaroslav Vitku
 *
 */
public class ImportanceEpsGreedyFloat extends ImportanceEpsilonGreedy<Float>{

	public ImportanceEpsGreedyFloat(ActionSetInt actions, ImportanceBasedConfig config) {
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
