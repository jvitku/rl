package org.hanns.rl.discrete.observer.stats.impl;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.dataStructure.impl.PreAllocatedMultiDimension;
import org.hanns.rl.discrete.observer.AbsSardaProspObserver;


/**
 * <p>Similar to {@link org.hanns.rl.discrete.observer.impl.BinaryCoverage}, but here
 * the Observer counts number of states, which have SOME KNOWLEDGE learned. That is, 
 * the observer counts states with at least one action with non-zero utility.</p>
 * 
 * <p>Note: this method provides only conservative estimate of the actual number 
 * of states with some knowledge! Because of computational costs (and the fact
 * that observer and the learning Algorithm are separated) only knowledge on 
 * the current state is checked! This means that eligibility trace can have better
 * performance than this observer says.</p>  
 * 
 * @author Jaroslav Vitku
 *
 */
public class KnowledgeCoverage extends AbsSardaProspObserver{

	public final String name = "KnowledgeCoverage";
	public final String explanation = "Value from [0,1] telling how many" +
			"states from the state space have non-zero utility value for some action.";
	
	private final int noStates;
	private final int[] sizes;
	protected final FinalQMatrix<Double> q;

	// uses the same data Structure as QMatrix, but with booleans indicating presence of a knowledge
	private final PreAllocatedMultiDimension<Boolean> knowledgePresent;

	/**
	 * Initialize with a vector of dimension sizes (that is number
	 * of values for each variable, without actions)
	 * 
	 * @param varSizes
	 * @param q QMatrix of the learning algorithm, this expects double values
	 * @see org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix
	 */
	public KnowledgeCoverage(int[] varSizes, FinalQMatrix<Double> q){

		this.q = q;
		knowledgePresent = new PreAllocatedMultiDimension<Boolean>(varSizes, 0, false);
		sizes = varSizes.clone();

		int tmp = varSizes[0];
		for(int i=1; i<varSizes.length; i++){
			tmp = tmp * varSizes[i];
		}
		noStates = tmp;
	}

	@Override
	public void observe(int prevAction, float reward, int[] currentState, int futureAction){
		
		step++;
		
		// here, if there is some non-zero utility value in the current state, mark as covered
		if(this.someKnowledgeFound(currentState))
			knowledgePresent.setValue(currentState, true);
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
		int vis = this.getNoCoveredStates();
		if(vis/noStates>1){	// this would mean some error
			System.err.println("KnowledgeCoverage: WARNING: visited more states than "
					+ "there is expected in the state space!");
			return 1;
		}
		return (float)vis/(float)noStates;
	}

	/**
	 * Get the absolute number of knowledgePresent states
	 * @return total number of states knowledgePresent so far
	 */
	public int getNoCoveredStates(){
		return this.noCovered(sizes, new int[sizes.length], 0);
	}

	/**
	 * This sums up all knowledgePresent coordinates in the map. 
	 * @param sizes number of values for each variable
	 * @param coords coordinates used in recursion, call e.g. with zeros 
	 * @param dimension used in the recursion, call with 0
	 * @return number of knowledgePresent states
	 */
	private int noCovered(int[] sizes, int[] coords, int dimension){

		if(dimension == sizes.length){
			if(knowledgePresent.readValue(coords))
				return 1;
			else return 0;
		}

		int sum = 0;
		int[] c;

		// in this dimension (e.g. 0 on the start) call this for all childs
		for(int i=0; i<sizes[dimension]; i++){
			c = coords.clone(); 
			c[dimension] = i;
			sum = sum + noCovered(sizes, c, dimension+1);
		}
		return sum;
	}

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
		knowledgePresent.setAllVals(false);
	}
	
	@Override
	public void hardReset(boolean randomize) {
		this.softReset(randomize);
	}
	
	
	@Override
	public String getName() { return name;	}

	@Override
	public String getDescription() { return explanation;	}
}

