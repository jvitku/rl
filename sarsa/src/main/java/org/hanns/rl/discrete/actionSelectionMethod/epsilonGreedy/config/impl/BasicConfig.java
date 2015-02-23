package org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl;

import org.hanns.rl.discrete.actionSelectionMethod.AbstractASMConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.BasicEpsilonGeedyConf;

/**
 * Basic configuration of epsilon-greedy algorithm. 
 * 
 * 
 * @author Jaroslav Vitku
 *
 */
public class BasicConfig extends AbstractASMConfig implements BasicEpsilonGeedyConf {

	public static final double DEF_EPSILON = 0.5;
	private double minEpsilon = DEF_MINEPSILON;

	// even in case of maximum importance, the randomization should occur  
	public static final double DEF_MINEPSILON = 0.1;


	private double epsilon = DEF_EPSILON;
	private boolean explorationEnabled = true;
	@Override
	public void setExplorationEnabled(boolean enable) { this.explorationEnabled = enable; }

	@Override
	public boolean getExplorationEnabled() { return this.explorationEnabled; }

	@Override
	public void setEpsilon(double value) {
		value = super.checkRange("epsilon", 0, 1, value);
		this.epsilon = value;
	}

	@Override
	public double getEpsilon() {
		if(this.explorationEnabled)
			return this.epsilon;
		else{
			return this.minEpsilon;
		}
	}


	@Override
	public void fireParamChanged() {}

	@Override
	public void setImportance(float importance) {
		// does nothing here
	}

	@Override
	public float getImportance() {
		// unused	
		return 0; 
	}
}

