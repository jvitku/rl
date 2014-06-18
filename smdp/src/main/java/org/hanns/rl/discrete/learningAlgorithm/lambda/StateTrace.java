package org.hanns.rl.discrete.learningAlgorithm.lambda;

import ctu.nengoros.network.common.Resettable;

/**
 * Simple memory for recently visited environment state-action pairs in the Q-matrix.
 * The used pushes state, action pair, the StateTrace concatenates these, thus
 * creating the coordinates in the Q-matrix which can be used. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface StateTrace extends Resettable{
	
	/**
	 * Remember new state, if memory exceeded, delete the 
	 * oldest one and push this on the top.
	 * @param stae state that has been just visited
	 * @param action action executed in the state 
	 */
	public void push(int[] stae, int action);
	
	/**
	 * Return the current size of state trace, 
	 * corresponds to the number of states pushed, 
	 * not necessarily to the max capacity of the trace.
	 * @return current number of states remembered 
	 */
	public int size();
	
	/**
	 * Return the maximum number of states that can be stored
	 * @return capacity of the memory
	 */
	public int getCapacity();
	
	/**
	 * Set the max number of states that can be remembered.
	 * @param n capacity of the memory
	 */
	public void setCapacity(int n);
	
	/**
	 * Get the state coordinates of i-th state in the memory 
	 * @param i index of the state stored
	 * @return state on the i-th position (state visited before i steps)
	 */
	public int[] get(int i); 

}
