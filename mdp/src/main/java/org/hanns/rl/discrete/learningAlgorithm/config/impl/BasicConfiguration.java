package org.hanns.rl.discrete.learningAlgorithm.config.impl;

import org.hanns.rl.discrete.learningAlgorithm.config.LearningConfig;

/**
 * Basic configuration of the Q-learning learning algorithm.
 * 
 * @author Jaroslav Vitku
 *
 */
public class BasicConfiguration implements LearningConfig{

	private boolean learningEnabled = true;

	public static final double DEF_ALPHA=0.5;
	public static final double DEF_GAMMA=0.3;
	
	// stochastic return predictor settings
	private double alpha = DEF_ALPHA;
	private double gamma = DEF_GAMMA;

	@Override
	public void setAlpha(double alpha) {
		if(!this.checkRange("alpha", 0, 1, alpha)){
			if(alpha<0)
				alpha = 0;
			else
				alpha = 1;
		}
		boolean changed = (this.alpha!= alpha);
		this.alpha = alpha;
		if(changed)
			this.fireParameterChanged();
	}

	@Override
	public double getAlpha() { return alpha; }

	@Override
	public void setGamma(double gamma) {
		if(!this.checkRange("gamma", 0, 1, gamma)){
			if(gamma<0)
				gamma = 0;
			else
				gamma = 1;
		}
		boolean changed = (this.gamma != gamma);
		this.gamma = gamma;
		if(changed)
			this.fireParameterChanged();
	}

	@Override
	public double getGamma() {return gamma; }

	@Override
	public void setLearningEnabled(boolean enable) {
		this.learningEnabled = enable;
		this.fireParameterChanged();
	}

	@Override
	public boolean getLearningEnabled() { return this.learningEnabled; }

	@Override
	public void fireParameterChanged() {}

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
		//System.err.println("Config ERROR, the parameter "+paramName+" should be from" +
		//		" interval ["+from+","+to+"], not "+newVal);
		return false;
	}

	@Override
	public void softReset(boolean randomize) { this.fireParameterChanged(); }

	@Override
	public void hardReset(boolean randomize) { this.softReset(randomize); }

}
