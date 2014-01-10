package org.hanns.rl.discrete.observer.impl;

import java.util.Random;

/**
 * 
 * The same as {@link BinaryCoverage}, which is not informative enough.
 * Here, each step: one randomly selected tale is set to unknown. 
 * Therefore the agent has one step to discover one tale, but also some other
 * tale is forgotten. This forces agent to cover the biggest area by the 
 * smallest amount of steps.
 * 
 * @author Jaroslav Vitku
 */
public class BinaryCoverageForgetting extends BinaryCoverage{

	Random r;

	/**
	 * Initialize with a vector of dimension sizes (that is number
	 * of values for each variable, without actions)
	 * 
	 * @param varSizes
	 * @see org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix
	 */
	public BinaryCoverageForgetting(int[] varSizes){
		super(varSizes);
		r = new Random();
	}

	@Override
	public void observe(int prevAction, float reward, int[] currentState, int futureAction){

		visited.setValue(currentState, true);	// mark visited state
		
		visited.setValue(this.randomState(), false);	// forget one random visited state each step 
		
		//System.out.println("binary coverage of: "+this.getProsperity()+ " no visited: "
			//	+this.getNoVisitedStates()+" of "+this.noStates);
	}
	
	private int[] randomState(){
		int[] rs = new int[sizes.length];
		for(int i=0; i<rs.length; i++){
			// generate random coord in range of the dimension
			rs[i] = r.nextInt(sizes[i]); 	
		}
		return rs;
	}
	
}
