package org.hanns.rl.discrete.ros.sarsa.config;

import org.hanns.rl.discrete.observer.stats.combined.BinaryCoverageForgettingReward;
import org.hanns.rl.discrete.ros.sarsa.QLambda;

/**
 * Publishes: {composed prosperity, BinaryCoverageForgetting, MCR}
 * 
 * @author Jaroslav Vitku
 *
 */
public class QlambdaCoverageForgettingReward extends QLambda{

	/**
	 * Instantiate the ProsperityObserver
	 */
	@Override
	protected void registerProsperityObserver(){
		o = new BinaryCoverageForgettingReward(this.states.getDimensionsSizes());
		//o = new KnowledgeChange(this.states.getDimensionsSizes(), q);
		//o = new ForgettingCoverageChangeReward(this.states.getDimensionsSizes(),q);
		observers.add(o);
	}

}
