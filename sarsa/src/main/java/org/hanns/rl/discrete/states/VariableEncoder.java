package org.hanns.rl.discrete.states;

/**
 * Receives real raw values from the ROS network <0,1> and encodes them
 * into integer values used by the learning algorithms.
 * 
 * The VariableEncoder is supposed to be used by the StateVariable class in 
 * the method {@link org.hanns.rl.discrete.states.StateVariable#setRawValue(float)}.
 *   
 * @author Jaroslav Vitku
 *
 */
public interface VariableEncoder {
	
	
	/**
	 * Encode the raw (received) value to the integer value of the variable.
	 * @param raw value received from the ROS network
	 * @return encoded integer value, this should be in range <0,{@link #getNumValues()}>.  
	 */
	public int encode(float raw);
	
	/**
	 * Return the number of possible values of encoded variable
	 * @return number of values that this variable can have
	 */
	public int getNumValues();

}
