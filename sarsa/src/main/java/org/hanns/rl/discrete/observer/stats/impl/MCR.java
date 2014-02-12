package org.hanns.rl.discrete.observer.stats.impl;

import ctu.nengoros.network.node.observer.stats.AbsProsperityObserver;


/**
 * Computes the Mean Cumulative Reward (MCR), that is: 
 * average binary received reinforcement per step.
 * 
 * @author Jaroslav Vitku
 *
 */
public class MCR extends AbsProsperityObserver{

	public final String name = "MCR";
	public final String explanation = "Value from [0,1] defining" +
			"how often is a reward received per step (1=reward each step).";
	
	private int rewards = 0;
	
	@Override
	public void observe(int prevAction, float reward, int[] currentState,
			int futureAction) {
		
			rewards++;
	}

	@Override
	public float getProsperity() { return (float)(rewards/step); }

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
		rewards=0;
	}
	
	@Override
	public String getName() { return name;	}

	@Override
	public String getDescription() { return explanation;	}
}
