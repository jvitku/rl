package org.hanns.rl.discrete.states;

import org.hanns.rl.common.exceptions.MessageFormatException;


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
	 * Return the variable of the specified name
	 * @param name name of the var.
	 * @return StateVariable of the specified name
	 */
	public StateVariable getVarByName(String name);
	

	/**
	 * Get the current values of the state variables.
	 * For each dimension there is one value, the value is from range
	 * <0,N-1>, where N is size of the corresponding dimension (num. of values).
	 * @return current value of the variable for each dimension - describes the world state.
	 */
	public int[] getValues();
	
	/**
	 * Set raw data to the variables. 
	 * @param values array of received raw values to be set 
	 * @throws MessageFormatException if the message has incorrect format (wrong size of array)
	 */
	public void setRawData(float[] values) throws MessageFormatException;
	
	
	/**
	 * Get value of variable by index
	 * @param index
	 * @return encoded value of the sate variable
	 */
	public int getValueOf(int index);
	
	/**
	 * return the value of a variable specified by name
	 * @param name name of the variable
	 * @return encoded value of the state variable
	 */
	public int getValueOf(String name);
	
	
}
