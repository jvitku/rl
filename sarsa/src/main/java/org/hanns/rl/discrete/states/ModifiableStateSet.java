package org.hanns.rl.discrete.states;

import java.util.HashMap;
import java.util.LinkedList;

import org.hanns.rl.common.exceptions.FinalParamException;

/**
 * This holds set of state variables. This set describes state
 * of the world for the algorithms. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface ModifiableStateSet {

	/**
	 * The number of state variables registered by the learning algorithm.
	 * This is e.g. identical to the dimensionality of the Q-learning matrix. 
	 * @return number of variables
	 */
	public int getNumVariables();
	
	/**
	 * The same as {@link #getVariables()}, but these values are mapped to the 
	 * {@link StateVariable} names.
	 * @return map mapping variable names to their current values
	 */
	public HashMap<String, StateVariable> getVariableMap();

	/**
	 * Ordered list of all variables.
	 * @return list of variables
	 */
	public LinkedList<StateVariable> getVariables();

	/**
	 * This adds new variable to the FinalStateSet (this would e.q. add new dimension 
	 * to the Q-learning algorithm). The number of values has to be specified.
	 *   
	 * @param numValues number of potential values
	 * @throws FinalParamException Some algorithms may support changing the number of world variables.
	 */
	public void addNewVariable(String name, int numValues) throws FinalParamException;
	
}
