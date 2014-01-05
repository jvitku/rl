package org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.EpsilonGreedyConfig;

/**
 * Basic configuration of epsilon-greedy algorithm. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class BasicConfig implements EpsilonGreedyConfig {

	public static final double DEF_EPSILON = 0.5;
	public static final double DEF_MINEPSILON = 0.2;


	private double epsilon = DEF_EPSILON;
	private double minEpsilon = DEF_MINEPSILON;
	private boolean explorationEnabled = true;

	@Override
	public void setMinEpsilon(double min) {
		if(!this.checkRange("minEpsilon", 0, 1, min))
			return;
		this.minEpsilon = min;
	}

	@Override
	public double getMinEpsilon(double min) { return this.minEpsilon; }

	@Override
	public void setExplorationEnabled(boolean enable) { this.explorationEnabled = enable; }

	@Override
	public boolean getExplorationEnabled() { return this.explorationEnabled; }

	@Override
	public void setEpsilon(double value) {
		if(!this.checkRange("epsilon", 0, 1, value))
			return;
		this.epsilon = value;
	}

	@Override
	public double getEpsilon() { return this.epsilon; }


	/**
	 * Check if a new value to a given parameter is in correct range, if not, log error
	 * @param paramName name of the parameter
	 * @param from lower bound inclusive
	 * @param to upper bound inclusive
	 * @param newVal new value to be set
	 * @return true if the new value is in range
	 */
	public boolean checkRange(String paramName, double from, double to, double newVal){
		if(newVal>=from && newVal<=to)
			return true;
		System.err.println("Config ERROR, the parameter "+paramName+" should be from" +
				" interval ["+from+","+to+"], not "+newVal);
		return false;
	}

}

