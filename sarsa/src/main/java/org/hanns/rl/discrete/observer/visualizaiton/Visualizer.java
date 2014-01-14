package org.hanns.rl.discrete.observer.visualizaiton;

import org.hanns.rl.discrete.observer.Observer;

/**
 * Represents visualization utility. Typically, the visualization of data should not 
 * occur each time step, since it is computationally expensive. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface Visualizer extends Observer{
	
	/**
	 * How many details to visualize
	 * @param details the higher number, the more details, and 0 should mean totally silent
	 */
	public void setVisDetails(int details);
	
	/**
	 * Return the number of details to be visualized
	 * @return 0 means no visualization update
	 */
	public int getVisDetails();
}
