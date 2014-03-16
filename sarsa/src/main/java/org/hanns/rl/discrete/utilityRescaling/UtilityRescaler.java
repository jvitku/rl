package org.hanns.rl.discrete.utilityRescaling;

/**
 * Get array of utility values, re-scale them based on the current situation and return new values.
 *    
 * @author Jaroslav Vitku
 *
 */
public interface UtilityRescaler {

	/**
	 * Gets array of action Utilities, re-scales these and returns
	 * array with new values.
	 *  
	 * @param utilities array of utility values stored in the Q(s,a) matrix
	 * @return array of the same length as utilities, but with modified values 
	 * (e.g. based on importance)
	 */
	public float[] rescale(float[] utilities);

}
