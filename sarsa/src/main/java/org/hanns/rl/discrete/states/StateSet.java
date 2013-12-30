package org.hanns.rl.discrete.states;

import java.util.HashMap;

import org.hanns.rl.exceptions.FinalParamException;

public interface StateSet {
	

	/**
	 * The number of state variables registered by the learning algorithm.
	 * This is e.g. identical to the dimensionality of the Q-learning matrix. 
	 * @return number of variables
	 */
	public int getNumVariables();
	
	
	/**
	 * The same as {@link #getValues()}, but these values are mapped to the StateVariable names
	 * @return map mapping variable names to their current values
	 */
	public HashMap<String, Integer> getNamedValues();
	

	/**
	 * This adds new variable to the StateSetold (this would e.q. add new dimension 
	 * to the Q-learning algorithm). The number of values has to be specified.
	 *   
	 * @param numValues number of potential values
	 * @throws FinalParamException Some algorithms may support changing the number of world variables.
	 */
	public void addNewVariable(String name, int numValues) throws FinalParamException;
	
	
}
