package org.hanns.rl.discrete.observer.impl;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.dataStructure.impl.PreAllocatedMultiDimension;
import org.hanns.rl.discrete.observer.Observer;

/**
 * This computes binary occurrence of the agent for all states.
 * Therefore the prosperity of 0.5 means that the agent was at least once
 * at 50% of states.  
 * 
 * @author Jaroslav Vitku
 */
public class BinaryCoverage implements Observer{

	private final int noStates;
	
	private final int[] sizes;
	
	// uses the same data Structure as QMatrix
	private final PreAllocatedMultiDimension<Boolean> visited;
	
	/**
	 * Initialize with a vector of dimension sizes (that is number
	 * of values for each variable, without actions)
	 * 
	 * @param varSizes
	 * @see org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix
	 */
	public BinaryCoverage(int[] varSizes){
		visited = new PreAllocatedMultiDimension<Boolean>(varSizes, 0, false);
		sizes = varSizes.clone();
		
		int tmp = varSizes[0];
		for(int i=1; i<varSizes.length; i++){
			tmp = tmp * varSizes[i];
		}
		noStates = tmp;
	}

	@Override
	public void observe(int prevAction, float reward, int[] currentState, int futureAction){
		// mark visited state
		visited.setValue(currentState, true);
	}

	@Override
	public float getProsperity() {
		
		int visited = this.noVisited(sizes, new int[sizes.length], 0);
		return visited/noStates;
	}
	
	public int getNoVisitedStates(){
		return this.noVisited(sizes, new int[sizes.length], 0);
	}
	
	/**
	 * This sums up all visited coordinates in the map. 
	 * @param sizes number of values for each variable
	 * @param coords coordinates used in recursion, call e.g. with zeros 
	 * @param dimension used in the recursion, call with 0
	 * @return number of visited states
	 */
	private int noVisited(int[] sizes, int[] coords, int dimension){
		
		if(dimension == sizes.length){
			if(visited.readValue(coords))
				return 1;
			else return 0;
		}
		
		int sum = 0;
		int[] c;
		
		// in this dimension (e.g. 0 on the start) call this for all childs
		for(int i=0; i<sizes[dimension]; i++){
			c = coords.clone(); 
			c[dimension] = i;
			sum = sum + noVisited(sizes, c, dimension+1);
		}
		return sum;
	}
}
