package org.hanns.rl.discrete.learningAlgorithm.qLearning.config.impl;

import org.hanns.rl.discrete.learningAlgorithm.qLearning.config.QLearningConfig;

/**
 * Basic configuration of the Q-learning learning algorithm.
 * 
 * @author Jaroslav Vitku
 *
 */
public class BasicConfiguration implements QLearningConfig{

	private boolean learningEnabled = true;

	// stochastic return predictor settings
	private double alpha;
	private double gamma;

	@Override
	public void setAlpha(double alpha) {
		this.alpha = alpha;
		this.fireParameterChanged();
	}

	@Override
	public double getAlpha() { return alpha; }

	@Override
	public void setGamma(double gamma) {
		this.gamma = gamma;
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
	public boolean getLearningEnabled() {return this.learningEnabled; }

	@Override
	public void fireParameterChanged() {}

}
