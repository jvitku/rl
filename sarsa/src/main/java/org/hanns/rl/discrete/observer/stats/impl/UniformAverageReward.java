package org.hanns.rl.discrete.observer.stats.impl;

import org.hanns.rl.discrete.observer.stats.AbsProsperityObserver;

/**
 * Basic observer which logs only positive rewards without considering 
 * any action importance etc. The {@link #getProsperity()} returns value
 * which represents average reward per step received. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class UniformAverageReward extends AbsProsperityObserver{

	public static final String name = "BinaryAverageReward";
	public static final String explanation = "Value from [0,1] telling" +
			"how often some reward is received per step, 1=reward " +
			"received each step.";

	private int steps = 0; 			// simulation step
	private float totalReward = 0; 	// total reward received 

	@Override
	public void observe(int prevAction, float reward, int[] currentState,
			int futureAction) {

		steps++;
		if(reward>0){
			totalReward += reward;
		}
	}

	@Override
	public float getProsperity() {
		if(steps>0)
			return totalReward/steps;
		return 0;
	}

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
		totalReward = 0;
	}

}
