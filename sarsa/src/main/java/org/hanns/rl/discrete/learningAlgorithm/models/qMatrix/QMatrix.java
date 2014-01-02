package org.hanns.rl.discrete.learningAlgorithm.models.qMatrix;

import org.hanns.rl.common.Resettable;

public interface QMatrix extends Resettable{

	public double[] getActionQValsForPreviousState();

	public double[] getActionQValsForCurrentState();


	public double[] getMaxActionValForCurrentState();

}
