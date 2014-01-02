package org.hanns.rl.discrete.learningAlgorithm.qLearning.impl;

import org.hanns.rl.common.exceptions.IncorrectDimensionsException;
import org.hanns.rl.discrete.learningAlgorithm.FinalModelLearningAlgorithm;
import org.hanns.rl.discrete.learningAlgorithm.LearningConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.impl.PreAllocatedFinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.config.QLearningConfig;

/**
 * QLearning algorithm over the model with final number of actions and state set.
 * 
 * @author Jaroslav Vitku
 *
 */
public class FinalModelQlearning implements FinalModelLearningAlgorithm{

	private final String mess = "FinalModelQLearning:" +
			" incorrect dimenion sizes for "+this.numActions+
			" actions and "+this.stateSizes.length+" states";
	
	private int numActions;
	private int[] stateSizes;
	private QLearningConfig config;
	private FinalQMatrix<Double> q;
	
	private int[] prevState;
	private int prevAction;
	
	/**
	 * Get the number of actions available, array defining how many values
	 * can each particular state can have and set the data structures.s
	 * @param stateSizes array defining how many values can each particular state
	 * variable can have
	 * @param numActions number of actions available to the agent
	 */
	public FinalModelQlearning(int[] stateSizes, int numActions, QLearningConfig config){
		
		this.config = config;
		this.stateSizes = stateSizes.clone();
		this.numActions = numActions;
		q = new PreAllocatedFinalQMatrix(stateSizes, numActions);
	}
	
	@Override
	public void performLearningStep(int action, float reward, int[] state) {
		if(!this.config.getLearningEnabled())
			return;
		
		if(this.prevState == null)
			this.init(state);

		// TODO Auto-generated method stub
		this.config.getAlpha();
		this.config.getGamma();
		
		double prev = q.get(prevState, prevAction); 
		double current = q.get(state, action);
		
		//TODO
		double newVal = prev+this.config.getAlpha();
		
//		double maxAction = this.maxInd(q.getActionValsInState(state));
		
		prevState = state;	// update last state
		

	}
	
	@Override
	public void init(int[] state) {this.prevState = state;	}

	@SuppressWarnings("unchecked")
	@Override
	public void setMatrix(FinalQMatrix<?> model) throws IncorrectDimensionsException {
		
		this.checkDimensions(model);
		
		if(!(model instanceof FinalQMatrix)){
			System.err.println("Given model not an instance of FinalQMatrix!");
			return;
		}
		q = (FinalQMatrix<Double>) model;
	}

	@Override
	public FinalQMatrix<?> getMatrix() { return q; }

	@Override
	public void setModel(FinalQMatrix<?> mode, int numActions, int[] stateSizes) 
			throws IncorrectDimensionsException {
		this.numActions = numActions;
		this.stateSizes = stateSizes;
		this.setMatrix(mode);
	}

	@Override
	public void setConfig(LearningConfiguration config) {
		if(!(config instanceof QLearningConfig))
				System.err.println("FinalModelQLearning: The class QLearningCongig expected");
		this.config = (QLearningConfig) config;
	}

	@Override
	public LearningConfiguration getConfig() { return this.config; }
	
	private void checkDimensions(FinalQMatrix<?> qm) throws IncorrectDimensionsException{
		int[] dims = qm.getDimensionSizes();
		
		if(dims.length !=this.stateSizes.length+1)
			throw new IncorrectDimensionsException(mess);
		
		if(dims[dims.length-1]!=this.numActions)
			throw new IncorrectDimensionsException(mess);
		
		for(int i=0; i<dims.length-1; i++){
			if(dims[i]!=this.stateSizes[i])
				throw new IncorrectDimensionsException(mess+
						". dimension no. "+i+" has incorrect size.");
		}
	}
	
}
