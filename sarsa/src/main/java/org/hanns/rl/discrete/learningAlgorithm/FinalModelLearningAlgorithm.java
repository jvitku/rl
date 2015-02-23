package org.hanns.rl.discrete.learningAlgorithm;

import org.hanns.rl.common.exceptions.IncorrectDimensionsException;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;

import ctu.nengoros.network.common.Resettable;

/**
 * Learning algorithm with a final model of the world. 
 * This means final number of actions and number of state variables 
 * with their sizes. These parameters cannot be changed
 * during the simulation.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface FinalModelLearningAlgorithm extends LearningAlgorithmInt, Resettable{
	
	/**
	 * Similar to the {@link #setMatrix(FinalQMatrix)}, but this setups entire 
	 * model of the algorithm, that is:
	 * <ul>
	 * <li>Q(s,a) matrix, which may contain data already</li>
	 * <li>Number of actions available, which must correspond to 
	 * the last dimension of the Q(s,a)</li>
	 * <li>Array of sizes of each state variable. The sizes of dimension 
	 * sizes have to correspond to the Q(s,a) matrix (without the last dimension)</li>
	 * </ul>
	 * @param mode Q(s,a) matrix which may contain data. Number of dimensions 
	 * has to be equal to: stateSizes.length+1. Dimension sizes of N-1 dimensions
	 * have to correspond to the to stateSizes and the last dimension to the numActions. 
	 * @param numActions number of actions available for the learning
	 * @param stateSizes array defining available number of values for each state variable
	 * @throws IncorrectDimensionsException if the numActions and stateSizes do not correspond 
	 * to the given matrix dimensions
	 */
	public void setModel(FinalQMatrix<?> mode, int numActions, int[] stateSizes) throws IncorrectDimensionsException;
}
