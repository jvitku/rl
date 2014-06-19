package org.hanns.rl.discrete.observer.asm;

import ctu.nengoros.network.node.observer.Observer;

public interface AsmObserver extends Observer{

	/**
	 * This just logs or visualizes agents behavior if conditions are met. 
	 * 
	 * @param selectedAction action that is currently selected by the ASM
	 */
	public void observe(int selectedAction);

}
