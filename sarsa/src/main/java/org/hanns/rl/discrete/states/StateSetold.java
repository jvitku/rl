package org.hanns.rl.discrete.states;

import java.util.HashMap;

import org.hanns.rl.exceptions.FinalParamException;

/**
 * Defines the set of world states which is used by the learning algorithm.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface StateSetold {
	
	/**
	 * The number of state variables registered by the learning algorithm.
	 * This is e.g. identical to the dimensionality of the Q-learning matrix. 
	 * @return number of variables
	 */
	public int getNumVariables();
	
	/**
	 * Retrieve sizes of dimensions of the state representation 
	 * @return integer defining sizes of particular dimensions
	 */
	public int[] getDimensionsSizes();
	
	/**
	 * Get the current values of the state variables.
	 * For each dimension there is one value, the value is from range
	 * <0,N-1>, where N is size of the corresponding dimension (num. of values).
	 * @return current value of the variable for each dimension - describes the world state.
	 */
	public int[] getValues();
	
	/**
	 * The same as {@link #getValues()}, but these values are mapped to the StateVariable names
	 * @return map mapping variable names to their current values
	 */
	public HashMap<String, Integer> getNamedValues();
	
	/**
	 * Set the current state of the world. For each variable (dimension) set
	 * its value from the range specified by the {@link #getDimensionsSizes()}.
	 * @param vals values of all variables
	 * @see #getValues()
	 */
	public void setValues(int[] vals);
	
	
	/**
	 * Set the value of state variable with the specified index.
	 * @param index index of the variable 
	 * @param value value of the variable
	 */
	public void setValue(int index, int value);
	
	/**
	 * This adds new variable to the StateSetold (this would e.q. add new dimension 
	 * to the Q-learning algorithm). The number of values has to be specified.
	 *   
	 * @param numValues number of potential values
	 * @throws FinalParamException Some algorithms may support changing the number of world variables.
	 */
	public void addNewVariable(int numValues) throws FinalParamException;
	
	
}
