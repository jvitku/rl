package org.hanns.rl.discrete.ros.asm;

import org.hanns.rl.discrete.actionSelectionMethod.greedy.GreedyFloat;
import org.ros.node.ConnectedNode;

public class EpsilonGreedy extends AbstractASMNode{

	protected GreedyFloat selection;// action selection methods
	Float[] tmp;
	
	@Override
	protected void onNewDataReceived(float[] data) {
		
		for(int i=0; i<data.length; i++)	// TODO, make this nicer
			tmp[i] = data[i];
		
		int selected = selection.selectAction(tmp);
		super.executeAction(selected);
	}
	
	/**
	 * No configuration needed here
	 */
	@Override
	protected void buildConfigSubscribers(ConnectedNode connectedNode){
		
	}

	@Override
	protected void initializeASM() {
		selection = new GreedyFloat(this.actions);
		asm = selection;	// handled in the parent
		
		tmp = new Float[this.actions.getNumOfActions()];
	}

}

