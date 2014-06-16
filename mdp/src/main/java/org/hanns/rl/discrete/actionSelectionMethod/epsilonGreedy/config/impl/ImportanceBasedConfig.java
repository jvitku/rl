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

	// even in case of maximum importance, the randomization should occur  
	public static final double DEF_MINEPSILON = 0.1;
	public static final float DEF_IMPORTANCE = 0.3f;

	private double epsilon;
	private double minEpsilon = DEF_MINEPSILON;
	private boolean explorationEnabled = true;

	private float importance = DEF_IMPORTANCE;

	public ImportanceBasedConfig(){
		this.fireParamChanged();
	}

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
		importance = super.checkRange("importance", 0, 1, importance);
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

	/**
	 * Compute the epsilon as inverted importance, 
	 * the values of epsilon can be from the interval: [minEpsilon,1].
	 */
	@Override
	public void fireParamChanged() {
			
		if(importance==0){
			epsilon = 1;
			return;
		}
		double interval = 1-minEpsilon;
		epsilon = minEpsilon + interval * (1-importance);
	}

	@Override
	public float getImportance() { return this.importance; }

}
