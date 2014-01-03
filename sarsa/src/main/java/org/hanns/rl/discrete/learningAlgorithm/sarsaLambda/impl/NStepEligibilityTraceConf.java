package org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.impl;

import org.hanns.rl.discrete.learningAlgorithm.qLearning.config.impl.BasicConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.NStepEligibilityTraceConfig;

public class NStepEligibilityTraceConf extends BasicConfiguration implements NStepEligibilityTraceConfig{

	private int length;		// number of currently visited states to remember 
	private double lambda;	// decay factor for the trace in time

	public NStepEligibilityTraceConf(int length){
		this.length = length;
	}

	@Override
	public int getEligibilityLength() { return length; }

	@Override
	public void setEligibilityLength(int length) { this.length = length; }

	@Override
	public void setLamda(double lambda) { 
		if(lambda<0){
			System.err.println("NStepEligibilityTraceConf: ERROR: lambda has to " +
					"be from interval of <0,1>, not "+lambda);
			lambda = 0;
		}else if(lambda>1){
			System.err.println("NStepEligibilityTraceConf: ERROR: lambda has to " +
					"be from interval of <0,1>, not "+lambda);
			lambda = 1;
		}
		this.lambda = lambda;
	}

	@Override
	public double getLabda() { return this.lambda; }
}
