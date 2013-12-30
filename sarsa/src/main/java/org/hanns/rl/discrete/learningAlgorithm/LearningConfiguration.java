package org.hanns.rl.discrete.learningAlgorithm;

/**
 * Configuration of general learning algorithm. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface LearningConfiguration {
	
	/**
	 * Ability to turn on/off the learning. 
	 * 
	 * @param enable should the algorithm update its model?
	 */
	public void setLearningEnabled(boolean enable);
	
	public boolean getLearningEnabled();
}
