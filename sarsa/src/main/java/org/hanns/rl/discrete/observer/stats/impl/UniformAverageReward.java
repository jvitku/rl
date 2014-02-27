package org.hanns.rl.discrete.observer.stats.impl;

import org.hanns.rl.discrete.observer.AbsSardaProspObserver;


/**
 * Basic observer which logs only positive rewards without considering 
 * any action importance etc. The {@link #getProsperity()} returns value
 * which represents average reward per step received. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class UniformAverageReward extends AbsSardaProspObserver{

	public final String name = "BinaryAverageReward";
	public final String explanation = "Value from [0,1] telling" +
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
		if(steps==0)
			return 0;
		if(totalReward/steps>1)
			return 1;
		return totalReward/steps;
	}

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
		totalReward = 0;
	}

	@Override
	public void hardReset(boolean randomize) {
		this.softReset(randomize);
	}


	@Override
	public String getName() { return name;	}

	@Override
	public String getDescription() { return explanation;	}

}
