package org.hanns.rl.discrete.learningAlgorithm.lambda.impl;

import org.hanns.rl.discrete.learningAlgorithm.config.impl.BasicConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.lambda.NStepLambdaConfig;

public class NStepQLambdaConfImpl extends BasicConfiguration implements NStepLambdaConfig{

	private int length = 5;			// number of currently visited states to remember 
	private double lambda = 0.1;	// decay factor for the trace in time

	private double[] decays;		// pre-computed decays (gammaT) for traces

	public NStepQLambdaConfImpl(int length, double lambda){
		this.length = length;
		this.lambda = lambda;
		this.fireParameterChanged();
	}

	public NStepQLambdaConfImpl(int length){
		if(length<1){
			System.err.println("NStepQLambdaConfImpl: Trace length should be more than 0!");
			return;
		}
		this.length = length;
		this.fireParameterChanged();
	}

	@Override
	public int getEligibilityLength() { return length; }

	@Override
	public void setEligibilityLength(int length) {
		if(length<1){
			System.err.println("NStepQLambdaConfImpl: Trace length should be more than 0!");
			return;
		}
		boolean changed = length!=this.length;
		this.length = length;
		if(changed)
			this.fireParameterChanged();
	}

	@Override
	public void setLambda(double lambda) {
		if(!super.checkRange("lambda", 0, 1, lambda)){
			if(lambda<0){
				lambda = 0;
			}else if(lambda>1){
				lambda = 1;
			}
		}
		boolean changed = lambda!=this.lambda;
		this.lambda = lambda;
		if(changed)
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

	@Override
	public void softReset(boolean randomize) {
		this.fireParameterChanged();
	}

	@Override
	public void hardReset(boolean randomize) { this.softReset(randomize); }


}
