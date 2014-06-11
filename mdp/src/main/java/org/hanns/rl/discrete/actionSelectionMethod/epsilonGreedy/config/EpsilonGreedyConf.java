package org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config;

import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethodConfig;

public interface EpsilonGreedyConf extends ActionSelectionMethodConfig{
	
	/**
	 * Get the value of epsilon parameter
	 * @return current value of the parameter
	 */
	public double getEpsilon();

}
