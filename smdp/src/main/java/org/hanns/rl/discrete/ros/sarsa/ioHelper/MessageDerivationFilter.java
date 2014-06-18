package org.hanns.rl.discrete.ros.sarsa.ioHelper;

import ctu.nengoros.network.common.Resettable;


/**
 * <p>This should be placed between the RL and Nengoros on incoming state representation.
 * It does similar job to simple derivator: only change of state (from incoming message)
 * is registered as a new state. In case that the action had no effect, there is defined
 * maximum number of steps to wait between the passing identical state to the RL node.</p>
 * 
 * <p>This ensures that the RL node will not process old states. It is required only when used 
 * with the Nengoros. In the ROS network (closed loop with immediate and unique response to 
 * the action) it is not necessary.</p>   
 * 
 * Note that entire message is "derivated" (that means float values including the reward).
 * 
 * @author Jaroslav Vitku
 *
 */
public interface MessageDerivationFilter extends Resettable {
	
	/**
	 * Set the maximum number of identical received states before sending the 
	 * state to the RL node. This defines maximum delay (counted in ROS messages~Nengoros 
	 * simulation steps) which can take action -> newState loop. In case of simple
	 * example: GridWorld - RL in the Nengoros, the length of loop is 1.
	 * 
	 * @param len number of steps to wait before passing massage with unchanged state to 
	 * the RL algorithm. That is: how many identical states to hold before publishing.
	 */
	public void setMaxClosedLoopLength(int len);
	
	/**
	 * @see #setMaxClosedLoopLength(int)
	 * @return max length of action->state closed loop 
	 */
	public int getMaxloopLength();
	
	/**
	 * This registers new message and checks whether the message is different
	 * from the last one, if yes (or the messages not changed for {@link #getMaxloopLength()}
	 * steps) return true.
	 * 
	 * @param message message received from the ROS network
	 * @return true if the message should be passed to the RL node
	 */
	public boolean newMessageShouldBePassed(float[] message);

}
