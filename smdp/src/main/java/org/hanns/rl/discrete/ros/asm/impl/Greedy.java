package org.hanns.rl.discrete.ros.asm.impl;

import org.hanns.rl.discrete.actions.ActionSetInt;
import org.ros.node.ConnectedNode;
import org.hanns.rl.discrete.actionSelectionMethod.greedy.GreedyDouble;
import org.hanns.rl.discrete.ros.asm.AbstractASM;
import org.hanns.rl.discrete.ros.asm.AbstractASMDouble;

/**
 * 
 * Implements ASM (Action Selection Method) in the ROS node. 
 * This is simple Greedy ASM, which selects action with the highest utility value and passes
 * the resulting data in 1ofN code. 
 * 
 * @see {@link AbstractASM}
 * 
 * @author Jaroslav Vitku
 *
 */
public class Greedy extends AbstractASMDouble{

	public static final String name = "GreedyASM";

	/**
	 * Here it is important
	 */
	@Override
	protected void initializeASM(){
		asm = new GreedyDouble((ActionSetInt)this.actions);
	}

	/**
	 * Nothing to do here
	 */
	@Override
	protected void buildASMSumbscribers(ConnectedNode connectedNode) {}

}



