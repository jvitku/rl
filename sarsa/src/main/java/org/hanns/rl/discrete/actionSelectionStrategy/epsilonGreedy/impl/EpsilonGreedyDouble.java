package org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.impl;

import org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.EpsilonGreedy;
import org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.config.EpsilonGreedyConfig;
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

	// TODO: this selection could be randomized: if a==b return a with p=0.5
	@Override
	protected boolean better(Double a, Double b) { return a>b; }
}
