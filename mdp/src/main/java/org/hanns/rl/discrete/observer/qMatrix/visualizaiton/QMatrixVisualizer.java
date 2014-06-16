package org.hanns.rl.discrete.observer.qMatrix.visualizaiton;

import org.hanns.rl.discrete.observer.qMatrix.QMatrixObserver;


/**
 * For visualization of Q(s,a) matrix. This represents utility values of all 
 * actions in a given state. So the rounded action values or best action 
 * in the state can be displayed. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface QMatrixVisualizer extends QMatrixObserver{
	
	/**
	 * If the rounding is disabled, the true value of action values is displayed
	 * @param enabled whether to enable rounding (true by default)
	 */
	public void setRoundingEnabled(boolean enabled);
	
	/**
	 * It could be possible to represent the actions by a graphical symbol, rather than
	 * only index.
	 * @param remaps array of graphical symbols, where each symbol corresponds to the action  
	 */
	public void setActionRemapping(String[] remaps);

}
