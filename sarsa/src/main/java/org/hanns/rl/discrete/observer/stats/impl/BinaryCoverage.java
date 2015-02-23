package org.hanns.rl.discrete.observer.stats.impl;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.dataStructure.impl.PreAllocatedMultiDimension;
import org.hanns.rl.discrete.observer.AbsSardaProspObserver;

import ctu.nengoros.network.node.observer.stats.ProsperityObserver;

/**
 * This computes binary occurrence of the agent for all states.
 * Therefore the prosperity of 0.5 means that the agent was at least once
 * at 50% of states.  
 * 
 * @author Jaroslav Vitku
 */
public class BinaryCoverage extends AbsSardaProspObserver{

	public final String name = "BinaryCoverage";
	public final String explanation = "Value from [0,1] telling how many" +
			"states from the state space were visited at least once.";
	
	protected final int noStates;

	protected final int[] sizes;

	// uses the same data Structure as QMatrix
	protected final PreAllocatedMultiDimension<Boolean> visited;

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

		step++;

		// mark visited state
		visited.setValue(currentState, true);

		if(this.shouldVis  && step % visPeriod==0)
			System.out.println("binary coverage of: "+this.getProsperity()+ " no visited: "
					+this.getNoVisitedStates()+" of "+this.noStates);
	}

	@Override
	public float getProsperity() {
		int vis = this.getNoVisitedStates();
		if(vis/noStates>1){
			System.err.println("BinaryCOverage: WARNING: visited more states than there"
					+ " is expected in the state space!");
			return 1;
		}
		return (float)vis/(float)noStates;
	}

	/**
	 * Get the absolute number of visited states
	 * @return total number of states visited so far
	 */
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

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
		visited.setAllVals(false);
	}
	
	@Override
	public void hardReset(boolean randomize) {
		this.softReset(randomize);
	}
	

	@Override
	public ProsperityObserver[] getChilds() {
		System.err.println("ERROR: no childs available");
		return null;
	}
	
	@Override
	public String getName() { return name;	}

	@Override
	public String getDescription() { return explanation;	}
}
