package org.hanns.rl.discrete.ros.srp.sarsa.config;

import org.hanns.rl.discrete.observer.stats.combined.ForgettingCoverageChangeReward;
import org.hanns.rl.discrete.ros.learning.qLearning.QLambda;

/**
 * Publishes: {composed prosperity, BinaryCoverageForgetting, MCR, KnowledgeChange}
 *  
 * @author Jaroslav Vitku
 *
 */
public class QLambdaKnowledgeChange extends QLambda{

	/**
	 * Instantiate the ProsperityObserver
	 */
	@Override
	protected void registerProsperityObserver(){
		//o = new BinaryCoverageForgettingReward(this.states.getDimensionsSizes());
		//o = new KnowledgeChange(this.states.getDimensionsSizes(), q);
		o = new ForgettingCoverageChangeReward(this.states.getDimensionsSizes(),q);
		observers.add(o);
	}
}
