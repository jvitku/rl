package org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.impl;

import org.hanns.rl.discrete.learningAlgorithm.qLearning.config.impl.BasicConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.NStepQLambdaConfig;

public class NStepQLambdaConfImpl extends BasicConfiguration implements NStepQLambdaConfig{

	private int length = 5;			// number of currently visited states to remember 
	private double lambda = 0.1;	// decay factor for the trace in time

	private double[] decays;		// pre-computed decays (gammaT) for traces

	public NStepQLambdaConfImpl(int length, double lambda){
		this.length = length;
		this.lambda = lambda;
		this.fireParameterChanged();
	}

	public NStepQLambdaConfImpl(int length){
		this.length = length;
		this.fireParameterChanged();
	}

	@Override
	public int getEligibilityLength() { return length; }

	@Override
	public void setEligibilityLength(int length) { 
		this.length = length;
		this.fireParameterChanged();
	}

	@Override
	public void setLambda(double lambda) { 
		if(lambda<0){
			System.err.println("NStepQLambdaConfImpl: ERROR: lambda has to " +
					"be from interval of <0,1>, not "+lambda);
			lambda = 0;
		}else if(lambda>1){
			System.err.println("NStepQLambdaConfImpl: ERROR: lambda has to " +
					"be from interval of <0,1>, not "+lambda);
			lambda = 1;
		}
		this.lambda = lambda;
		this.fireParameterChanged();
	}

	@Override
	public double getLambda() { return this.lambda; }

	/**
	 * Computes decays for trace of length n with 
	 * a current values of alpha and error. We will define error at 
	 * the current time step as: 
	 * err(s,a)t = gammaT*lambdaT*err(s,a)t. 
	 * 
	 * So the value of gammaT*lambdaT is pre-computed
	 * in the array {@link #decays} for each time step. 
	 * The actual time-step is on the index 0.
	 */
	private void computeDecays(){

		this.decays = new double[this.getEligibilityLength()];
		decays[0] = 1;	// the first state is one 

		for(int i=1; i<decays.length; i++){
			decays[i] = decays[i-1]*this.getGamma()*lambda;
		}
	}

	@Override
	public void fireParameterChanged(){
		this.computeDecays();
	}

	@Override
	public double[] getdecays() { return this.decays; }

}
