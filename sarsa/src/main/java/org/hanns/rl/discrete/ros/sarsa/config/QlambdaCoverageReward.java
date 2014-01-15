package org.hanns.rl.discrete.ros.sarsa.config;

import org.hanns.rl.discrete.observer.stats.combined.ForgettingCoverageChangeReward;
import org.hanns.rl.discrete.ros.sarsa.QLambda;

public class QlambdaCoverageReward extends QLambda{

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
