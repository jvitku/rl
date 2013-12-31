package org.hanns.rl.discrete.learningAlgorithm.qlearning.model.impl;

import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.learningAlgorithm.qlearning.model.FinalQMatrix;
import org.hanns.rl.discrete.states.impl.BasicFinalStateSet;

public class BasicFinalQMatrix implements FinalQMatrix{

	//double
	BasicFinalActionSet actionVars;
	BasicFinalStateSet stateVars;
	
	private int numVariables, numActions;
	private int numStates;
	
	private int[] dimensionSIzes;
	
	private Dimension<Double> d;
	
	/**
	 * Specify the action and state sets to initialize the QMatrix 
	 * @param aSet
	 * @param sSet
	 */
	public BasicFinalQMatrix(BasicFinalActionSet aSet, BasicFinalStateSet sSet){
		this.actionVars = aSet;
		this.stateVars = sSet;
		
		this.numActions = aSet.getNumOfActions();
		this.numVariables = sSet.getNumVariables();
		this.dimensionSIzes = sSet.getDimensionsSizes();
		
		
		// init the data structure
		d = new Dimension<Double>(dimensionSIzes, 0, new Double(0));
		
	}
	
	@Override
	public void softReset(boolean randomize) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hardReset(boolean randomize) {
		// TODO Auto-generated method stub
		
	}
}
