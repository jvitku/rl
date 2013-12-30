package org.hanns.rl.discrete.states;

/**
 * This is list of state variables with final number of variables and their 
 * dimensions.
 *  
 * @author Jaroslav Vitku
 *
 */
public interface FInalStateSet {

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
	public void setValueOf(int index, int value);
	
	/**
	 * Get value of variable by index
	 * @param index
	 */
	public void getValueOf(int index);
	
}
