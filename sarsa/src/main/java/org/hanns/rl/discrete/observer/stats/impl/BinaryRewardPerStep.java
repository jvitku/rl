package org.hanns.rl.discrete.observer.stats.impl;

import ctu.nengoros.network.node.observer.stats.AbsProsperityObserver;


/**
 * Computes average binary received reinforcement per step.
 * 
 * @author Jaroslav Vitku
 *
 */
public class BinaryRewardPerStep extends AbsProsperityObserver{

	public final String name = "BinaryRewardPerStep";
	public final String explanation = "Value from [0,1] defining" +
			"how often is a reward received per step (1=reward each step).";
	
	private int steps = 0;
	private int rewards = 0;
	
	@Override
	public void observe(int prevAction, float reward, int[] currentState,
			int futureAction) {
		steps++;
		
		if(reward>0)
			rewards++;
	}

	@Override
	public float getProsperity() { return (float)rewards/(float)steps; }

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
