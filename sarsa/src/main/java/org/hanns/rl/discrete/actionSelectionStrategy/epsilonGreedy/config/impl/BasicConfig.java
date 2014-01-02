package org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.config.impl;

import org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.config.EpsilonGreedyConfig;

/**
 * Basic configuration of epsilon-greedy algorithm. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class BasicConfig implements EpsilonGreedyConfig {

	private double epsilon = 0;
	private double minEpsilon = 0;
	private boolean explorationEnabled = true;

	@Override
	public void setMinEpsilon(double min) { this.minEpsilon = min; }

	@Override
	public double getMinEpsilon(double min) { return this.minEpsilon; }

	@Override
	public void setExplorationEnabled(boolean enable) { this.explorationEnabled = enable;	}

	@Override
	public boolean getExplorationEnabled() { return this.explorationEnabled; }

	@Override
	public void setEpsilon(double value) { this.epsilon = value;	}

	@Override
	public double getEpsilon() { return this.epsilon; }

}

