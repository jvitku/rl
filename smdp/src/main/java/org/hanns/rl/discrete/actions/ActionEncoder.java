package org.hanns.rl.discrete.actions;

import org.hanns.rl.common.exceptions.DecoderException;


/**
 * This class translates actions into real outputs. 
 * The EncoderAndActionSet holds own ActionSetInt which it uses. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface ActionEncoder {
	
	/**
	 * Receives index of an action, produces array of floats
	 * representing the action. This array is to be sent
	 * over the ROS network.
	 * 
	 * @param index index of the action in the action set. -1 means no action.
	 * @return encoded action to be passed to the output
	 */
	public float[] encode(int index);
	
	/**
	 * The actions can be also decoded from their outer representation.
	 * This may not be used by the algorithms.
	 * 
	 * @param data message representing the action
	 * @return index of action in the actionSet
	 * @throws DecoderException if data could not be decoded into action index
	 */
	public int decode(float [] data) throws DecoderException; 
	

}
