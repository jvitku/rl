package org.hanns.rl.discrete.observer.stats;

import org.hanns.rl.discrete.observer.Observer;

/**
 * Observes behaviour of the algorithm and publishes its quality.
 *  
 * @author Jaroslav Vitku
 *
 */
public interface ProsperityObserver extends Observer{

	/**
	 * Value of successfulness of the agent (node). This can be computed
	 * from the data logged by the method {@link #observe(int, float, int[], int)}.
	 * 
	 * Possible candidates are: measure of coverage of the available state-space,
	 * average reward received etc. 
	 * 
	 * @return value from interval [0,1] determining how successful the algorithm is 
	 */
	public float getProsperity();

	/**
	 * ProsperityObserver value can be composed of sub-observers' values, 
	 * so return all child observers.
	 * @return array of child observers that are potentially used 
	 * to compute the observed value.
	 */
	public ProsperityObserver[] getChilds();
	
	/**
	 * Returns description of the observer, this should explain what the 
	 * Observers value means in more detail.
	 * @return explanation of a purpose the observed value.
	 */
	public String getDescription();
}
