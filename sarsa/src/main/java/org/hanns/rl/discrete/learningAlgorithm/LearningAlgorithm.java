package org.hanns.rl.discrete.learningAlgorithm;

import org.hanns.rl.common.Resettable;
import org.hanns.rl.common.exceptions.IncorrectDimensionsException;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;

/**
 * Interface for the learning algorithm. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface LearningAlgorithm extends Resettable {
	
	/**
	 * Retrieve the action performed, reward obtained and observation of the current
	 * world state. The algorithm remembers 
	 *   
	 * @param action index of action that was performed
	 * @param reward value of reward received from the environment (architecture) 
	 * @param state state observed on inputs
	 */
	public void performLearningStep(int action, float reward, int[] state);	
	
	/**
	 * Initialize the algorithm. Mainly set the starting state of the environment.
	 * @param state state in which the simulation starts
	 */
	public void init(int[] state);
	
	/**
	 * Set the configuration of this learning algorithm.
	 * @param config configuration of parameters of this algorithm.
	 */
	public void setConfig(LearningConfiguration config);
	
	/**
	 * Get the configuration class for this algorithm
	 * @return current configuration
	 */
	public LearningConfiguration getConfig();
	
	/**
	 * Sets the Q(s,a) matrix that will be used by the algorithm from now on. 
	 * @param model Q(s,a) matrix whose dimensions have to correspond to the 
	 * number of actions, number of states and number of state values
	 */
	public void setMatrix(FinalQMatrix<?> model) throws IncorrectDimensionsException;
	
	/**
	 * Get the Q(s,a) matrix currently used by the algorithm. 
	 * @return Q(s,a) matrix currently used by the algorithm, containing 
	 * the model learned so far.
	 */
	public FinalQMatrix<?> getMatrix();
}

