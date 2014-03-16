package org.hanns.rl.discrete.ros.asm;

import org.hanns.rl.discrete.actionSelectionMethod.greedy.GreedyFloat;
import org.ros.node.ConnectedNode;

import ctu.nengoros.util.SL;

/**
 * Greedy Action Selection Method: select the action with the highest utility.
 * 
 * Importance input is ignored in this case.
 *  
 * @author Jaroslav Vitku
 */
public class Greedy extends AbstractASMNode{

	protected GreedyFloat selection;// action selection methods
	Float[] tmp;
	
	@Override
	protected void onNewDataReceived(float[] data) {
		
		for(int i=0; i<data.length; i++)	// TODO, make this nicer
			tmp[i] = data[i];
		
		int selected = selection.selectAction(tmp);
		
		if(logPeriod%(step)==0)
			System.out.println("Received these utilities: "+SL.toStr(data)+" selecting the action no.: "+selected);
		
		super.executeAction(selected);
	}
	
	/**
	 * No configuration needed here
	 */
	@Override
	protected void buildConfigSubscribers(ConnectedNode connectedNode){}

	@Override
	protected void initializeASM() {
		selection = new GreedyFloat(this.actions);
		asm = selection;	// handled in the parent
		
		tmp = new Float[this.actions.getNumOfActions()];
	}
}
