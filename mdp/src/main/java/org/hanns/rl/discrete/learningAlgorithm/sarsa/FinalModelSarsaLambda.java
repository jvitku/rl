package org.hanns.rl.discrete.learningAlgorithm.sarsa;

import org.hanns.rl.discrete.learningAlgorithm.lambda.NStepLambdaConfig;
import org.hanns.rl.discrete.learningAlgorithm.lambda.impl.AbstractFinalModelNStepLambda;

/**
 * SARSA-Lambda algorithm is an improvement of SARSA with eligibility trace.
 * 
 * @author Jaroslav Vitku
 *
 */
public class FinalModelSarsaLambda extends AbstractFinalModelNStepLambda{

	public FinalModelSarsaLambda(int[] stateSizes, int numActions, NStepLambdaConfig conf) {
		super(stateSizes, numActions, conf);
	}

	@Override
	protected double getNewMaxActionVal(Double[] newActions, int selectedAction) {
		return newActions[selectedAction];
	}
}
