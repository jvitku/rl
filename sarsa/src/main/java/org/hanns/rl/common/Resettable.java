package org.hanns.rl.common;

/**
 * Reset can be called from inside the ROS node, or e.g. potentially  externally from 
 * within the ROS network. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface Resettable {
	
	/**
	 * Should re-initialize main components of the simulation, 
	 * but preserve learned data (models) 
	 * 
	 * @param randomize whether to randomize the initial state
	 */
	public void softReset(boolean randomize);
	
	/**
	 * Hard reset should restart all running components and
	 * delete all data collected so far (e.g. erase models learned).
	 * 
	 * @param randomize randomize new values?
	 */
	public void hardReset(boolean randomize);

}
