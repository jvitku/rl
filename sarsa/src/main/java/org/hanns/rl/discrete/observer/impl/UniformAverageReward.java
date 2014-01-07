package org.hanns.rl.discrete.observer.impl;

import org.hanns.rl.discrete.observer.Observer;

/**
 * Basic observer which logs only positive rewards without considering 
 * any action importance etc. The {@link #getProsperity()} returns value
 * which represents average reward per step received. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class UniformAverageReward implements Observer{

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

}
