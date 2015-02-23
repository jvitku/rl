package org.hanns.rl.discrete.learningAlgorithm.models.qMatrix;

import ctu.nengoros.network.common.Resettable;


/**
 * <p>QMatrix which expects final set of state variables and final state of actions. The 
 * dimensionality of the matrix is therefore un-modifiable.</p>
 * 
 * <p>The Q(s,a) matrix maps the space of s (states) and a (actions) to real values.
 * The number on the particular position defines expected sum future outcomes if 
 * the agent takes a given action in a given state. So the higher value means
 * the better outcome.</p>
 * 
 * The following requirements are posed to the QMatrix:
 * <ul>
 * <li>get value set value from given coordinates</li>
 * <li>access array of action values in a given state (required by action selection method</li>
 * <li>reset values in the matrix (randomize/not randomize)</li>
 * <li>get action values in a given state</li>
 * </ul>
 * 
 * TODO split this to the QMatrix interface, which is now deleted.
 * 
 * @author Jaroslav Vitku
 */
public interface FinalQMatrix<E> extends Resettable{

	/**
	 * Returns the array containing sizes of all dimensions.
	 * The last dimension corresponds to the array of actions
	 * @return array of dimension sizes
	 */
	public int[] getDimensionSizes();
	
	/**
	 * Return the number of actions that can be used
	 * @return number of actions used
	 */
	public int getNumActions();
	
	/**
	 * Return the number of registered state variables defining the world state
	 * @return number of state variables
	 */
	public int getNumStateVariables();

	/**
	 * Return variables defining: min value of randomized
	 * variable and range in which the values are generated.
	 * Therefore all randomly generated values in the matrix
	 * by the method {@link #hardReset(boolean)} are from range
	 * of [min,min+range]
	 * @return two values, min and range of randomly generated values
	 */
	public double[] getRandomizeRange();
	
	/**
	 * Return the default value which is placed during
	 * the initialization the data structure, or during the 
	 * call of method {@link #hardReset(boolean)}
	 * @return default value in the matrix
	 */
	public Double getDefaultValue();
	
	/**
	 * Set value to given coordinates in the matrix.
	 * 
	 * @param coordinates array of coordinates if size states.length+1 (actions) 
	 * @param value value to be set
	 */
	public void set(int[] coordinates, E value);
	
	/**
	 * The same as {@link #set(int[], Object)}, but here, the state and action
	 * are set separate.
	 * @param state representation of an observed state
	 * @param action index of action (to be) performed
	 * @param value value to be set
	 */
	public void set(int[] state, int action, E value);
	
	/**
	 * Get value from the specified coordinates in the matrix
	 * @param coordinates array of coordinates if size states.length+1 (actions)
	 * @return value stored on a given coordinates
	 */
	public E get(int[] coordinates);
	
	/**
	 * The same as {@link #get(int[])}, but here, the action and state is defined
	 * separately.
	 * @param state representation of an observed state
	 * @param action index of action (to be) performed
	 * @return the value in the matrix
	 */
	public E get(int[] state, int action);

	/**
	 * Get array of Q(s,a) values for all actions in a given state 
	 * @param states array of coordinates describing the current state of the world (states.length)
	 * @return array of Q(s,a) values in given state for all available actions
	 */
	public E[] getActionValsInState(int[] states);

	/**
	 * If the method {@link #hardReset(boolean)} with parameter randomize
	 * set to false is called, this value will be placed on all
	 * places in the matrix.
	 * 
	 * @param defValue default value after randomizing the matrix 
	 */
	public void setDefaultValue(E defValue);
	
	/**
	 * If the method {@link #hardReset(boolean)} with the parameter
	 * randomize set to true, uniformly distributed random variables 
	 * from the specified range will be set to the matrix. 
	 * 
	 * @param minValue lower boundary of random values  
	 * @param maxValue upper boundary of random values 
	 */
	public void setRandomizationParameters(E minValue,E maxValue);
}
