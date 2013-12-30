package org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.impl;

import org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.ActionSelectionConfig;

/**
 * Basic configuration of epsilon-greedy algorithm. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class BasicConfig implements ActionSelectionConfig {

	private double minEpsilon = 0;

	@Override
	public void setMinEpsilon(double min) { this.minEpsilon = min; }

	@Override
	public double getMinEpsilon(double min) { return this.minEpsilon; }

}
