package org.hanns.rl.discrete.learningAlgorithm.qlearning;

import org.hanns.rl.common.Resettable;

/**
 * Provides services with the QMatrix for the algorithm. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface QMatrixManager extends Resettable{
	
	public double[] getActionQValsForState(int[] stateVals);
	
	/**
	 * Values in the previous stae of the environment
	 * @return
	 */
	public double[] getActionQValsForPreviousState();
	
	public double[] getActionQValsForCurrentState();
	
	
	public double[] getMaxActionValForCurrentState();
	
	///TOOD

}
