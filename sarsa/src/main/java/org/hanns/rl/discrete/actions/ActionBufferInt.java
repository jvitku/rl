package org.hanns.rl.discrete.actions;

import org.hanns.rl.common.Resettable;

/**
 * 
 * <p>Action buffer is used for storing past actions executed by the agent.
 * The n-step action buffer may be necessary in larger systems where 
 * a delay of n-steps is present in the action-response closed loop.
 * E.g. in the Nengoros simulator, the closed loop between RL and GridWorld
 * has delay of 1-step.</p>
 * 
 * <p>The action buffer should support methods push(pushes newly executed action
 * on top and discards the oldest one) and get(gets the oldest action in the buffer).</p>
 * 
 * @author Jaroslav Vitku
 *
 */
public interface ActionBufferInt extends Resettable{
	
	/**
	 * Push one new action into the buffer (the oldest one is discarded). 
	 * Therefore if size is 1, no buffering occurs.
	 * 
	 * @param action index of action that is pushed into the buffer (remembered)
	 */
	public void push(int action);
	
	/**
	 * Read the last (oldest) action in the buffer
	 * @return index of action that is the oldest remembered (e.g. if size 
	 * of the buffer is 2, return the previously executed action)
	 */
	public int read();
	
	/**
	 * Set the length of the action buffer
	 * @param length 1 means no buffering
	 */
	public void setLength(int length);
	
	/**
	 * Get number of actions remembered by this buffer
	 * @return number of actions buffered, where 1 means no buffering
	 */
	public int getLength();

	/**
	 * 
	 * @return true if the list does not contain any action
	 */
	public boolean isEmpty();
	
}
