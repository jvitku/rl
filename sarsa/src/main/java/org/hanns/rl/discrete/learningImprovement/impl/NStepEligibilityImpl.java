package org.hanns.rl.discrete.learningImprovement.impl;

import org.hanns.rl.discrete.learningAlgorithm.LearningConfiguration;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.config.QLearningConfig;
import org.hanns.rl.discrete.learningImprovement.NStepEligibilityTrace;
import org.hanns.rl.discrete.learningImprovement.StateTrace;

/**
 * 
 * TODO delete this:-)
 * The difference here can be in obtaining the Q(s,a) value compared to the link. 
 * Here, the greedy action value is selected always (the action selection can 
 * select different action). This corresponds to computing: "what is the best action
 * that can be made from this state after last action a and reward r?".   
 *  
 * @see <a href="http://www.tu-chemnitz.de/informatik/KI/scripts/ws0910/ml09_7.pdf">page 24 - Sarsa(lambda)</a>
 *  
 * @author Jaroslav Vitku
 *
 */
public class NStepEligibilityImpl implements NStepEligibilityTrace{

	private int n;
	private double lambda;
	
	private final QLearningConfig conf;
	FinalQMatrix<Double> q;
	
	private int[] prevState;
	double delta;				// one step error
	private int stepsTaken;		// number of steps from the beginning of the simulation

	private double[] decays;	// pre-computed decays (gammaT) for traces
	private StateTrace trace;	
	
	public NStepEligibilityImpl(int n, double lambda, QLearningConfig lc, FinalQMatrix<Double> q){
		this.setN(n);	
		this.setLamda(lambda);
		this.conf = lc;
		this.stepsTaken = 0;
		this.q = q;
		trace = new StateTraceImpl(this.n);	// place for storing states
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
	public void setN(int n) {
		if(n<1){
			System.err.println("Eligibility, ERROR: N has to be non-" +
					"negative number, not "+n+", setting trace length to 1");
			this.n = 0;
		}else{
			this.n = n;
		}
	}

	@Override
	public int getN() { return this.n; }

	@Override
	public void setLamda(double lambda) {
		if(this.lambda<0){
			System.err.println("Eligibility: ERROR: labda has to be in " +
					"range of <0,1>, not "+lambda+", setting to 0");
			this.lambda = 0;
		}else if(this.lambda>1){
			System.err.println("Eligibility: ERROR: labda has to be in " +
					"range of <0,1>, not "+lambda+", setting to 1");
			this.lambda = 1;
		}else{
			this.lambda = lambda;
		}
	}

	@Override
	public double getLabda() { return this.lambda; }

	@Override
	public void softReset(boolean randomize) {
		this.prevState = null;
		this.stepsTaken = 0;
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
		this.decays = new double[this.n];
		double gamma = this.conf.getGamma();
		decays[0] = gamma*lambda;
		for(int i=1; i<this.n; i++){
			decays[i] = decays[i-1]*gamma*lambda;
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
}
