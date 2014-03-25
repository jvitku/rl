package org.hanns.rl.discrete.observer.qMatrix.visualizaiton;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;

/**
 * Final state space visualization for the Q matrix of type double.
 * 
 * @author Jaroslav Vitku
 *
 */
public class FinalStateSpaceVisDouble extends FinalStateSpaceVis<Double>{

	public final String name = "FinalStateSpaceVisDouble";
	
	public FinalStateSpaceVisDouble(int[] dimSizes, int noActions, FinalQMatrix<Double> q) {
		super(dimSizes, noActions, q);
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
		return a.doubleValue()>b.doubleValue();
	}

	@Override
	public Double round(Double what, int how){
		int rd = (int)(what*how);
		double d = (double)rd;
		return d/how;
	}
	
	@Override
	public String getName(){ return name; }

}
