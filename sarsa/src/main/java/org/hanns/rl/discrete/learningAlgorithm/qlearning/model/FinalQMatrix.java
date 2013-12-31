package org.hanns.rl.discrete.learningAlgorithm.qlearning.model;

import org.hanns.rl.common.Resettable;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.states.impl.BasicFinalStateSet;

/**
 * QMatrix which expects final set of state variables and final state of actions. The 
 * dimensionality of the matrix is therefore un-modifiable.
 * 
 * The Q(s,a) matrix maps the space of s (states) and a (actions) to real values.
 * The number on the particular position defines expected sum future outcomes if 
 * the agent takes a given action in a given state. So the higher value means
 * the better outcome.
 * 
 * The following requirements are posed to the QMatrix:
 * <ul>
 * <li>access array of action values in a given state (required by action selection method</li>
 * <li>access the max value</li>
 * <li></li>
 * <li></li>
 * <li></li>
 * </ul>
 * 
 * @author Jaroslav Vitku
 *
 */
public interface FinalQMatrix extends Resettable{

	// TODO this should be in the constructor
	//public void init(BasicFinalActionSet aSet, BasicFinalStateSet sSet);
	

	
	
}
