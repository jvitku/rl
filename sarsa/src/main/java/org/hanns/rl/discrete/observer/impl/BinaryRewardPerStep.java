package org.hanns.rl.discrete.observer.impl;

import org.hanns.rl.discrete.observer.Observer;

/**
 * Computes average binary received reinforcement per step.
 * 
 * @author Jaroslav Vitku
 *
 */
public class BinaryRewardPerStep implements Observer{

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
		steps=0;
		rewards=0;
	}

	@Override
	public void hardReset(boolean randomize) { this.softReset(randomize); }
	
	@Override
	public Observer[] getChilds() {
		System.err.println("ERROR: no childs available");
		return null;
	}
}
