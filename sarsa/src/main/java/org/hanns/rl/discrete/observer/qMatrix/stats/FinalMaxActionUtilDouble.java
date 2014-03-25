package org.hanns.rl.discrete.observer.qMatrix.stats;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;

/**
 * Will write best utility value for each state in the file.  
 * 
 * @author Jaroslav Vitku
 *
 */
public class FinalMaxActionUtilDouble extends FinalMaxActionUtil<Double> {

	public final String name = "FinalMaxActionUtilDouble";
	
	public FinalMaxActionUtilDouble(int[] dimSizes, int noActions, FinalQMatrix<Double> q, String filename) {
		super(dimSizes, noActions, q, filename);
	}

	@Override
	protected boolean foundNonZero(Double[] values) {
		for(int i=0; i<values.length; i++)
			if(values[i]!=0)
				return true;
		return false;
	}

	@Override
	protected boolean better(Double a, Double b) {
		return a.doubleValue() > b.doubleValue();
	}

	@Override
	public String getName(){ return name; }

}
