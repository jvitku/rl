package org.hanns.rl.discrete.learningAlgorithm;

import ctu.nengoros.network.common.Resettable;

/**
 * Configuration of general learning algorithm. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface LearningConfiguration extends Resettable {
	
	/**
	 * This should be called from all methods that set some parameter.
	 * Some configurations (or algorithms) may need to recompute some
	 * parameters after changing some parameter.
	 */
	public void fireParameterChanged();
	
	/**
	 * Ability to turn on/off the learning. 
	 * 
	 * @param enable should the algorithm update its model?
	 */
	public void setLearningEnabled(boolean enable);
	
	public boolean getLearningEnabled();
}
