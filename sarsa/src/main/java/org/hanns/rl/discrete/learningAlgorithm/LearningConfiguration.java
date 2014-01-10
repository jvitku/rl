package org.hanns.rl.discrete.learningAlgorithm;

import org.hanns.rl.common.Resettable;
import org.hanns.rl.discrete.actions.ActionBufferInt;

/**
 * Configuration of general learning algorithm. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface LearningConfiguration extends Resettable {
	
	/**
	 * This should be called from all methods that set some parameter.
	 * Some configurations (or algorithms) may need to recompute some
	 * parameters after changing some parameter.
	 */
	public void fireParameterChanged();
	
	/**
	 * Ability to turn on/off the learning. 
	 * 
	 * @param enable should the algorithm update its model?
	 */
	public void setLearningEnabled(boolean enable);
	
	public boolean getLearningEnabled();
	
	
	/**
	 * <p>Action buffer is used for storing past actions executed by the agent.
	 * The n-step action buffer may be necessary in larger systems where 
	 * a delay of n-steps is present in the action-response closed loop.
	 * E.g. in the Nengoros simulator, the closed loop between RL and GridWorld
	 * has delay of 1-step.</p>
	 * 
	 * <p>The action buffer should support methods push(pushes newly executed action
	 * on top and discards the oldest one) and get(gets the oldest action in the buffer).</p>
	 * 
	 * <p>The buffer with default length should be created in the constructor of each config.</p>
	 * 
	 * @param number of actions that will be remembered by the algorithm
	 * @see org.hanns.rl.discrete.actions.ActionBufferInt
	 */
	public void setBuffer(ActionBufferInt buffer);
	
	/**
	 * @return return my action buffer
	 */
	public ActionBufferInt getBuffer();
}
