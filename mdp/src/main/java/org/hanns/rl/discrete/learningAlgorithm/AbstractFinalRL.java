package org.hanns.rl.discrete.learningAlgorithm;

import org.hanns.rl.common.exceptions.IncorrectDimensionsException;
import org.hanns.rl.discrete.learningAlgorithm.config.LearningConfig;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.impl.PreAllocatedFinalQMatrix;
import org.hanns.rl.discrete.states.FInalStateSet;

/**
 * Abstract class which implements common tasks for Reinforcement Learning
 * over model with finite number of dimensions and dimension sizes.
 *    
 * @author Jaroslav Vitku
 *
 */
public abstract class AbstractFinalRL implements FinalModelLearningAlgorithm{
	
	protected LearningConfig config;
	
	protected FinalQMatrix<Double> q;
	protected int numActions;
	protected int[] stateSizes;

	protected int[] prevState;

	protected final String mess = "FinalModelQLearning:" +
			" incorrect dimenion sizes";

	/**
	 * Here is the difference between SARSA and Q-Learning.
	 * Implement this method in order to obtain action value to be learned for 
	 * the next step.
	 *  
	 * @param newActions array of action values available in the new state
	 * @param selectedAction index of action that is selected to the next step
	 */
	protected abstract double getNewMaxActionVal(Double[] newActions, int selectedAction);
	
	
	
	/**
	 * Get the number of actions available, array defining how many values
	 * can each particular state can have and set the data structures.s
	 * @param stateSizes array defining how many values can each particular state
	 * variable can have
	 * @param numActions number of actions available to the agent
	 */
	public AbstractFinalRL(int[] stateSizes, int numActions, LearningConfig config){
		this.stateSizes = stateSizes;
		this.numActions = numActions;
		q = new PreAllocatedFinalQMatrix(stateSizes, numActions);
		
		this.config = config;
	}
	
	public AbstractFinalRL(FInalStateSet states, int numActions){
		this.stateSizes = states.getDimensionsSizes().clone();
		this.numActions = numActions;
		q = new PreAllocatedFinalQMatrix(stateSizes, numActions);
	}
	
	public AbstractFinalRL(int[] stateSizes, int numActions){
		this.stateSizes = stateSizes.clone();
		this.numActions = numActions;
		q = new PreAllocatedFinalQMatrix(stateSizes, numActions);
	}


	/**
	 * Here, the parameter futureAction is ignored and the learning computes with the 
	 * optimal policy (best action in the current state), no matter which action 
	 * has been chosen for execution.
	 */
	@Override
	public void performLearningStep(int prevAction, float reward, int[] newState, int newAction) {
		if(!this.config.getLearningEnabled())
			return;

		if(this.prevState == null)
			this.init(newState);

		// we were there and made the action
		double prevVal = q.get(prevState, prevAction);	
		// action values available now
		Double[] newActions  = q.getActionValsInState(newState);
		
		// Q-Lambda vs. SARSA: choose the action value to be learned 
		double nextActionVal = this.getNewMaxActionVal(newActions, newAction); 

		// compute the learning equation
		double learned = prevVal + this.config.getAlpha()*
				(reward+this.config.getGamma()*nextActionVal-prevVal);

		q.set(prevState, prevAction, learned);	// store the value
		prevState = newState.clone();		// update last state and action
	}

	@Override
	public void setConfig(LearningConfiguration config) {
		if(!(config instanceof LearningConfig))
			System.err.println("AbstractFinalRL: The class LearningConfiguration expected");
		this.config = (LearningConfig) config;
	}

	//@Override
	public LearningConfig getConfig() { return this.config; }
	
	@Override
	public void init(int[] state) { 
		this.prevState = state.clone();
	}

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
	public void softReset(boolean randomize) {
		this.prevState = null;
		q.softReset(randomize);
	}

	@Override
	public void hardReset(boolean randomize) {
		this.softReset(randomize);
		q.hardReset(randomize);
	}

	/**
	 * Return the index of action with the highest utility.
	 * @param actionVals array of actions in one state returned from the Q-matrix
	 * @return index of the action with the highest utility
	 */
	protected int maxInd(Double[] actionVals){
		int ind = 0;
		for(int i=0; i<actionVals.length; i++){
			if(actionVals[i].doubleValue() > actionVals[ind].doubleValue())
				ind = i;
		}
		return ind;
	}

	/**
	 * Check whether given Q-matrix has expected dimension sizes 
	 * @param qm FinalQMatrix<?>
	 * @throws IncorrectDimensionsException thrown if dimension sizes are not consistent
	 * with information expected here 
	 */
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
