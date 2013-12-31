package org.hanns.rl.discrete.states;


/**
 * State variable. Each variable has own encoder, which encodes
 * values received from the ROS network to integer values used by algorithms.  
 * 
 * @author Jaroslav Vitku
 *
 */
public interface StateVariable {
	
	/**
	 * Name should be final.
	 * 
	 * @return name of the variable (used for finding it in the ModifiableStateSet)
	 */
	public String getName();
	
	/**
	 * 
	 * @return encoded value of the variable (used by algorithms)
	 */
	public int getVal();

	
	/**
	 * This method should set the raw value of the variable and use own encoder
	 * to obtain also the encoded value.
	 * 
	 * @param value value received from the ROS network
	 */
	public void setRawValue(float value);
	
	/**
	 * Get non-encoded value, which was received over the network.
	 * Probably on interval <0,1>, probably not used.
	 * @return non-encoded value of the variable
	 */
	public float getRawValue();
	
	/**
	 * The number of variable values is required by learning algorithms and
	 * determined by the encoder (based on range and sampling).
	 *  
	 * @return number of possible values (should be encoded by integers from 0)
	 */
	public int getNumValues();
	
}
