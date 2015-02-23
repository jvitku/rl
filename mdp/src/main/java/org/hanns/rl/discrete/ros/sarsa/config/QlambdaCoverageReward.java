package org.hanns.rl.discrete.ros.sarsa.config;

import org.hanns.rl.discrete.observer.stats.combined.KnowledgeCoverageReward;
import org.hanns.rl.discrete.ros.sarsa.QLambda;

/**
 * Publishes: {composed prosperity, BinaryCoverageForgetting, MCR}
 * 
 * @author Jaroslav Vitku
 *
 */
public class QlambdaCoverageReward extends QLambda{

	/**
	 * Instantiate the ProsperityObserver
	 */
	@Override
	protected void registerProsperityObserver(){
		//o = new BinaryCoverageReward(this.states.getDimensionsSizes());
		o = new KnowledgeCoverageReward(this.states.getDimensionsSizes(), q);
		
		//o = new KnowledgeChange(this.states.getDimensionsSizes(), q);
		//o = new ForgettingCoverageChangeReward(this.states.getDimensionsSizes(),q);
		observers.add(o);
	}
}
