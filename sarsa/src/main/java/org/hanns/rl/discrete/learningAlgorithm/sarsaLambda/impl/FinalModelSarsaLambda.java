package org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.impl;

import org.hanns.rl.discrete.learningAlgorithm.AbstractFinalRL;
import org.hanns.rl.discrete.learningAlgorithm.LearningConfiguration;
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
 * <p>This is Naive Q(lambda) (or called McGovern's): here, compared to other versions of
 * the algorithm, the value of optimal action (highest utility in a given state)
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
public class FinalModelSarsaLambda extends AbstractFinalRL{

	double delta;				// one step error

	private StateTrace trace;	

	private NStepEligibilityTraceConfig conf;

	public FinalModelSarsaLambda(int[] stateSizes, int numActions, 
			NStepEligibilityTraceConfig conf){
		super(stateSizes, numActions);

		this.conf = conf;
		trace = new StateTraceImpl(this.conf.getEligibilityLength());
	}

	@Override
	public void performLearningStep(int prevAction, float reward, int[] newState, int newAction) {
		if(!this.conf.getLearningEnabled())
			return;

		if(this.prevState == null)
			this.prevState = newState.clone();

		// we were there and made the action
		double prevVal = q.get(prevState, prevAction);	
		// action values available now
		Double[] newActions  = q.getActionValsInState(newState);	
		// value of the best available action now
		//double maxNewActionVal = newActions[this.maxInd(newActions)];//naive	
		double maxNewActionVal = newActions[newAction];//naive

		// here goes the learning equation
		delta = reward + conf.getGamma()*maxNewActionVal - prevVal;

		trace.push(prevState,prevAction);	// store the previous state-action pair

		double value;

		// apply knowledge update to all states stored in the trace 
		for(int i=0; i<trace.size(); i++){

			// apply the eligibility trace to n previously visited state-aciton pairs
			value = q.get(trace.get(i))+conf.getdecays()[i]*delta*conf.getAlpha();
			// add to old value
			q.set(trace.get(i), value); 
		}
		prevState = newState.clone();		// update last state and action
	}

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);

		this.trace.softReset(randomize);
	}

	@Override
	public void hardReset(boolean randomize) {
		super.hardReset(randomize);

		this.trace.hardReset(randomize);
	}

	@Override
	public void init(int[] state) {
		super.init(state);
		this.trace.softReset(false);	// delete all traced states
	}

	@Override
	public void setConfig(LearningConfiguration config) {
		if(!(config instanceof NStepEligibilityTraceConfig)){
			System.err.println("FinalModelSarsa: ERROR: expected " +
					"NStepEligibilityTraceConfig congiguration!");
			return;
		}
		this.conf = (NStepEligibilityTraceConfig)config;
	}

	@Override
	public LearningConfiguration getConfig() { return this.conf; }


}
