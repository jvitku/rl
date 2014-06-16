package org.hanns.rl.discrete.learningAlgorithm.qLearning.impl;

import org.hanns.rl.discrete.learningAlgorithm.lambda.NStepLambdaConfig;
import org.hanns.rl.discrete.learningAlgorithm.lambda.impl.AbstractFinalModelNStepLambda;
import org.hanns.rl.discrete.states.FInalStateSet;

/**
 * Q-Lambda algorithm is an improvement of Q-Learning with the eligibility trace.
 * 
 * @author Jaroslav Vitku
 *
 */
public class FinalModelQLambda extends AbstractFinalModelNStepLambda{

	public FinalModelQLambda(int[] stateSizes, int numActions, NStepLambdaConfig conf) {
		super(stateSizes, numActions, conf);
	}

	public FinalModelQLambda(FInalStateSet set, int numActions, NStepLambdaConfig conf){
		super(set, numActions, conf);
	}
	

	@Override
	protected double getNewMaxActionVal(Double[] newActions, int selectedAction) {
		return newActions[this.maxInd(newActions)];
	}
}
