package org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.impl;

import org.hanns.rl.common.exceptions.IncorrectDimensionsException;
import org.hanns.rl.discrete.learningAlgorithm.FinalModelLearningAlgorithm;
import org.hanns.rl.discrete.learningAlgorithm.LearningConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.NStepEligibilityTraceConfig;
import org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.StateTrace;

/**
 * 
 * <p>The difference between backward SARSA(lambda) eligibility trace for state-action pairs
 *  compared to the Q-learning algorithm is that here, the agent learns multiple past
 *  state-action values simultaneously. This can speed-up the learning process significantly.
 *  The decay for updating the past state-action values is specified by the parameter lambda, 
 *  if the lambda is too big, the learning convergence can become unstable.</p>  
 *    
 * <p>Note: Here, compared to the algorithm in the doc folder, the greedy action value 
 * is selected always (the action selection can select different action). 
 * This corresponds to computing: "what is the best action that can be made from 
 * this state after last action a and reward r?".</p>   
 *  
 *  <p>Note: the final model means that the algorithm can be used only for Q-matrix
 *  with finite number of dimension and dimension sizes</p>
 *  
 * @see <a href="http://www.tu-chemnitz.de/informatik/KI/scripts/ws0910/ml09_7.pdf">page 24 - Sarsa(lambda)</a>
 *  
 * @author Jaroslav Vitku
 *
 */
public class FinalModelSarsaLambda implements FinalModelLearningAlgorithm{

	private int[] prevState;
	double delta;				// one step error

	private double[] decays;	// pre-computed decays (gammaT) for traces
	private StateTrace trace;	
	
	private FinalQMatrix<Double> q;
	private final NStepEligibilityTraceConfig conf;
	
	public FinalModelSarsaLambda(NStepEligibilityTraceConfig conf){
		this.conf = conf;
		this.q = null; // TODO add this to abstract class
		trace = new StateTraceImpl(this.conf.getEligibilityLength());
		this.computeDecays();
	}

	@Override
	public void performLearningStep(int action, float reward, int[] newState) {
		if(this.prevState == null)
			this.prevState = newState.clone();
		
		double prevVal = q.get(prevState, action);	// we were there and made the action
		Double[] currentActions  = q.getActionValsInState(newState);	// action values available now
		double maxActionVal = currentActions[this.maxInd(currentActions)];	// value of the best available action now

		delta = reward+ conf.getGamma()*maxActionVal - prevVal;
		trace.push(newState);
		
		for(int i=0; i<trace.size(); i++){
			
			// TODO here
			//double valuevtm =  
			//double val = conf.getAlpha()*decays[i]*trace.get(i);
		}
		
		prevState = newState.clone();		// update last state and action
	}

	@Override
	public void softReset(boolean randomize) {
		this.prevState = null;
		this.trace.softReset(randomize);
	}

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
		this.decays = new double[this.conf.getEligibilityLength()];
		double gamma = this.conf.getGamma();
		decays[0] = gamma*this.conf.getLabda();
		for(int i=1; i<this.conf.getEligibilityLength(); i++){
			decays[i] = decays[i-1]*gamma*this.conf.getLabda();
		}
	}
	
	private int maxInd(Double[] actionVals){
		int ind = 0;
		for(int i=0; i<actionVals.length; i++){
			if(actionVals[i] > actionVals[ind])
				ind = i;
		}
		return ind;
	}
	
	@Override
	public void hardReset(boolean randomize) {
		this.softReset(randomize);
		this.computeDecays();
		this.trace.hardReset(randomize);
	}

	@Override
	public void init(int[] state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConfig(LearningConfiguration config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LearningConfiguration getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMatrix(FinalQMatrix<?> model)
			throws IncorrectDimensionsException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FinalQMatrix<?> getMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setModel(FinalQMatrix<?> mode, int numActions, int[] stateSizes)
			throws IncorrectDimensionsException {
		// TODO Auto-generated method stub
		
	}
}
