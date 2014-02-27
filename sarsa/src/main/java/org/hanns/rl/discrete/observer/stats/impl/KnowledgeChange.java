package org.hanns.rl.discrete.observer.stats.impl;

import org.hanns.rl.discrete.actions.ActionSet;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.dataStructure.impl.PreAllocatedMultiDimension;
import org.hanns.rl.discrete.observer.AbsSardaProspObserver;

/**
 * <p>Value from [0,1] which is one minus knowledge change for step.
 * The knowledge change per step is telling how many best actions (in the current 
 * state) change (in the Q matrix) per step. This should show the 
 * knowledge convergence well: the bigger knowledge convergence, the higher prosperity.</p> 
 * 
 * <p>Store best actions for all possible states, detect if the knowledge about 
 * the best action changed in the current sate. Note that this is only heuristics, 
 * since the eligibility traces can change more states in one step than one.</p>
 * 
 * @author Jaroslav Vitku
 *
 */
public class KnowledgeChange extends AbsSardaProspObserver{

	public final String name = "KnowledgeChange";
	public final String explanation = "Value from [0,1] telling how many" +
			"best action (in the current state) change (in the Q matrix) per step."
			+ " This should show the knowledge convergence well.";

	protected final FinalQMatrix<Double> q;

	// store best actions for all possible states
	private final PreAllocatedMultiDimension<Integer> bestAction;
	
	private int changes;

	/**
	 * Initialize with a vector of dimension sizes (that is number
	 * of values for each variable, without actions)
	 * 
	 * @param varSizes size of particular dimensions
	 * @param q QMatrix of the learning algorithm, this expects double values
	 * @see org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix
	 */
	public KnowledgeChange(int[] varSizes, FinalQMatrix<Double> q){

		this.q = q;
		bestAction = new PreAllocatedMultiDimension<Integer>(varSizes, 0, ActionSet.NOOP);
		
		changes = 0;
		step = 0;
	}

	/**
	 * Here: check if the best action (not its utility value) changed in the current state. 
	 */
	@Override
	public void observe(int prevAction, float reward, int[] currentState, int futureAction){

		step++;

		// if no knowledge here, just suppose that nothing updated
		if(!this.someKnowledgeFound(currentState)){
			return;
		}
		
		// action values in the current state
		Double [] vals = q.getActionValsInState(currentState);
		int best = this.bextBestIndex(vals);
		//System.out.println("Kownedlge found and best is: "+best+" and my best is: "+this.bestAction.readValue(currentState));
		
		if(best == this.bestAction.readValue(currentState))
			return;
		
		// action changed here
		this.bestAction.setValue(currentState, best);
		this.changes++;
	}

	/**
	 * Find an index of the best action from the array
	 * @param vals array of action utilities
	 * @return index of the best action, supposes that there is already 
	 * some knowledge (non-zero utility)
	 */
	private int bextBestIndex(Double[] vals){
		int ind = ActionSet.NOOP;
		double tmp = 0;
		for(int i=0; i<vals.length; i++){
			if(vals[i] > tmp){
				ind = i;
				tmp = vals[i];
			}
		}
		return ind;
	}

	/**
	 * Check if there is some non-zero utility value in the state on given coordinates.
	 * @param coordinates coordinates of the state in the matrix
	 * @return true if there is some non-zero action utility 
	 */
	private boolean someKnowledgeFound(int[] coordinates){
		Double [] vals = q.getActionValsInState(coordinates);
		for(int i=0; i<vals.length; i++){
			if(vals[i] != 0)
				return true;
		}
		return false;
	}

	@Override
	public float getProsperity() {
		if(step==0)
			return 0;
		if(changes==0)
			return 0;
		double result = 1-(double)changes/(double)step;;
		if(result>1)
			return 1;
		return (float)result;
	}

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
	}

	@Override
	public void hardReset(boolean randomize) {
		super.hardReset(randomize);
		// delete all learned statistics
		this.bestAction.setAllVals(ActionSet.NOOP);
		step = 0;
		changes = 0;
	}

	@Override
	public String getName() { return name;	}

	@Override
	public String getDescription() { return explanation;	}
}

