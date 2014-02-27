package org.hanns.rl.discrete.observer.stats.impl;

import org.hanns.rl.discrete.observer.AbsSardaProspObserver;


/**
 * Computes the Mean Cumulative Reward (MCR), that is: 
 * average binary received reinforcement per step.
 * 
 * @author Jaroslav Vitku
 *
 */
public class MCR extends AbsSardaProspObserver{

	public final String name = "MCR";
	public final String explanation = "Value from [0,1] defining" +
			"how often is a reward received per step (1=reward each step).";

	private float rewards = 0;

	@Override
	public void observe(int prevAction, float reward, int[] currentState,
			int futureAction) {

		step++;
		if(reward>0)
			rewards = rewards + reward;
	}

	@Override
	public float getProsperity() { 
		if(step==0)
			return 0;
		if(rewards/step>1)
			return 1;
		return (float)(rewards/step);
	}

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
		rewards=0;
	}

	@Override
	public void hardReset(boolean randomize) {
		this.softReset(randomize);
	}

	@Override
	public String getName() { return name;	}

	@Override
	public String getDescription() { return explanation; }
}
