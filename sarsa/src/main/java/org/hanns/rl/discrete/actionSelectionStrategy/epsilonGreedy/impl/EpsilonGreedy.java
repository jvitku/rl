package org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.impl;

import org.hanns.rl.discrete.actionSelectionStrategy.ActionSelectionMethod;

public class EpsilonGreedy implements ActionSelectionMethod{
	

	private int maxInd(Double[] actionVals){
		int ind = 0;
		for(int i=0; i<actionVals.length; i++)
			if(actionVals[i]>actionVals[ind])
				ind = i;
		return ind;
	}

	@Override
	public int selectAction() {
		return 0;
		// TODO Auto-generated method stub
		
	}

	

}
