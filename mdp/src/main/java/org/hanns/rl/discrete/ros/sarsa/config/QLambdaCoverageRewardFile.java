package org.hanns.rl.discrete.ros.sarsa.config;

import org.hanns.rl.discrete.observer.qMatrix.stats.FinalMaxActionUtilDouble;
import org.hanns.rl.discrete.observer.qMatrix.stats.QMatrixFileWriter;

/**
 * 
 * The same as QlambdaCoverageReward, but this writes qMatrix to the file
 * 
 * @author Jaroslav Vitku
 *
 */
public class QLambdaCoverageRewardFile extends QlambdaCoverageReward{

	private QMatrixFileWriter writer;
	public static String filename = "generated-data/qmatrix.txt";

	/**
	 * Instantiate the ProsperityObserver and FileWriter
	 */
	@Override
	protected void registerProsperityObserver(){
		super.registerProsperityObserver();

		writer = new FinalMaxActionUtilDouble(
				super.states.getDimensionsSizes(), 
				super.actions.getNumOfActions(), 
				super.q, filename);

		observers.add(writer);
	}

	/**
	 * TODO: Write the resulting data once more here?
	 */
	@Override
	public void hardReset(boolean randomize) {
		super.hardReset(randomize);
	}

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
	}
}
