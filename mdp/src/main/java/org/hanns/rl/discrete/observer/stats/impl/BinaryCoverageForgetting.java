package org.hanns.rl.discrete.observer.stats.impl;

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

	public final String name = "BinaryCoverageForgetting";
	public final String explanation = "Value from [0,1] telling how many" +
			"states from the state space were visited. Each time step, one " +
			"state is forgotten, so this value is more informative than BinaryCoverage";
	
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

		step++;

		visited.setValue(currentState, true);	// mark visited state

		visited.setValue(this.randomState(), false);	// forget one random visited state each step 

		if(this.shouldVis  && step % visPeriod==0)
			System.out.println("binary coverage of: "+this.getProsperity()+ " no visited: "
					+this.getNoVisitedStates()+" of "+this.noStates);
	}

	private int[] randomState(){
		int[] rs = new int[sizes.length];
		for(int i=0; i<rs.length; i++){
			// generate random coord in range of the dimension
			rs[i] = r.nextInt(sizes[i]); 	
		}
		return rs;
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
	public String getName() { return name;	}

	@Override
	public String getDescription() { return explanation;	}
}
