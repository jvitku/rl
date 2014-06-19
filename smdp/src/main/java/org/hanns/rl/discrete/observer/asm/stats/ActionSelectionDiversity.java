package org.hanns.rl.discrete.observer.asm.stats;

import org.hanns.rl.discrete.observer.asm.AbstractAsmObserver;

/**
 * Premise that successful agent architecture (ASM particularly) should select
 * diverse actions, also not many identical actions in a row. 
 *
 * TODO implement this
 * TODO log to file with unique name
 * 
 * @author Jaroslav Vitku
 */
public class ActionSelectionDiversity extends AbstractAsmObserver{

	private static final String name = ActionSelectionDiversity.class.getSimpleName();
	
	public ActionSelectionDiversity(){
		super(name);
	}
	
	@Override
	public void observe(int selectedAction) {
		// TODO Auto-generated method stub

	}

}
