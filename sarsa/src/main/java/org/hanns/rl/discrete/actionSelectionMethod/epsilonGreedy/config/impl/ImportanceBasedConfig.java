package org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl;

import org.hanns.rl.discrete.actionSelectionMethod.AbstractASMConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.ImportanceBasedEpsilonGreedyConf;

/**
 * This is configuration of epsilon-greedy AMS, where the current value of epsilon
 * depends on the current value of importance of selected action.
 * If the importance is high, there is small space for randomization.
 * But even during the highest importance, there is still small randomization 
 * maintained.
 * 
 * @author Jaroslav Vitku
 *
 */
public class ImportanceBasedConfig extends AbstractASMConfig 
implements ImportanceBasedEpsilonGreedyConf{

	public static final double DEF_EPSILON = 0.5;
	// even in case of maximum importance, the randomization should occur  
	public static final double DEF_MINEPSILON = 0.1;


	private double epsilon = DEF_EPSILON;
	private double minEpsilon = DEF_MINEPSILON;
	private boolean explorationEnabled = true;

	private float importance = 0;

	@Override
	public void setMinEpsilon(double min) {
		min = super.checkRange("minEpsilon", 0, 1, min);
		this.minEpsilon = min;
		this.fireParamChanged();
	}

	@Override
	public double getMinEpsilon() { return this.minEpsilon; }

	@Override
	public void setExplorationEnabled(boolean enable) { 
		this.explorationEnabled = enable;
		this.fireParamChanged();
	}

	@Override
	public boolean getExplorationEnabled() { return this.explorationEnabled; }

	@Override
	public void setImportance(float importance) { 
		super.checkRange("importance", 0, 1, importance);
		this.importance = importance;
		this.fireParamChanged();
	}

	@Override
	public double getEpsilon() { 
		// if the exploration is completely disabled, return the min. epsilon val.
		if(!this.explorationEnabled){
			return this.minEpsilon;
		}
		return epsilon;
	}

	@Override
	public void fireParamChanged() {
		double rand = 1-importance; // how much randomize we can?
		if(rand<minEpsilon)
			epsilon = minEpsilon;
		epsilon = rand;
	}

	@Override
	public float getImportance() { return this.importance; }

}
