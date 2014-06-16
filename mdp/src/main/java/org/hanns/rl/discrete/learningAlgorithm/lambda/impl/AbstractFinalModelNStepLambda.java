package org.hanns.rl.discrete.learningAlgorithm.lambda.impl;

import org.hanns.rl.discrete.learningAlgorithm.AbstractFinalRL;
import org.hanns.rl.discrete.learningAlgorithm.LearningConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.lambda.NStepLambdaConfig;
import org.hanns.rl.discrete.learningAlgorithm.lambda.StateTrace;
import org.hanns.rl.discrete.states.FInalStateSet;

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
 *  <p>Also note that this learning algorithm ignores sub-zero rewards (handled as zero rewards).</p>
 *  
 * @see <a href="http://www.tu-chemnitz.de/informatik/KI/scripts/ws0910/ml09_7.pdf">page 24 - Sarsa(lambda)</a>
 *  
 * @author Jaroslav Vitku
 *
 */
public abstract class AbstractFinalModelNStepLambda extends AbstractFinalRL{

	double delta;				// one step error

	private StateTrace trace;	

	private NStepLambdaConfig conf;

	public AbstractFinalModelNStepLambda(FInalStateSet set, int numActions, NStepLambdaConfig conf){
		super(set, numActions);

		this.conf = conf;
		trace = new StateTraceImpl(this.conf.getEligibilityLength());
	}
	
	public AbstractFinalModelNStepLambda(int[] stateSizes, int numActions, NStepLambdaConfig conf){
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

		if(reward<0)	
			reward=0;
		
		// we were there and made the action
		double prevVal = q.get(prevState, prevAction);	
		// action values available now
		Double[] newActions  = q.getActionValsInState(newState);
		
		// add SARSA or QLearning here
		double newActionVal = this.getNewMaxActionVal(newActions, newAction);	

		// here goes the learning equation
		delta = reward + conf.getGamma()*newActionVal - prevVal;
		
		trace.push(prevState,prevAction);	// store the previous state-action pair

		double value;

		// apply knowledge update to all states stored in the trace 
		for(int i=0; i<trace.size(); i++){
			//System.out.println("value in the q is "+q.get(trace.get(i))+" trace: "+SL.toStr(trace.get(i)));
			
			// apply the eligibility trace to n previously visited state-action pairs
			value = q.get(trace.get(i)) + conf.getdecays()[i]*delta*conf.getAlpha();
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
		if(!(config instanceof NStepLambdaConfig)){
			System.err.println("AbstractFinalModelNStepLambda: ERROR: expected " +
					"NStepLambdaConfig congiguration!");
			return;
		}
		this.conf = (NStepLambdaConfig)config;
	}

	//@Override
	public NStepLambdaConfig getConfig() { return this.conf; }


}
