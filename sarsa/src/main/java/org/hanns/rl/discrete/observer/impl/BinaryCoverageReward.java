package org.hanns.rl.discrete.observer.impl;

import org.hanns.rl.discrete.observer.Observer;

/**
 * This weights BinaryConverage with the average binary reward (per step)
 * 50 to 50.
 * 
 * TODO this should be extended with average reward during high importance
 * and average coverage during low importance.
 * 
 * @author Jaroslav Vitku
 * @see ctu.nengoros.nodes.HannsNode
 */
public class BinaryCoverageReward implements Observer{

	BinaryCoverage cover;
	BinaryRewardPerStep rew;
	
	public BinaryCoverageReward(int[] varSizes){
		cover = new BinaryCoverage(varSizes);
		rew = new BinaryRewardPerStep();
	}
	
	@Override
	public void observe(int prevAction, float reward, int[] currentState,
			int futureAction) {
		
		cover.observe(prevAction, reward, currentState, futureAction);
		rew.observe(prevAction, reward, currentState, futureAction);
	}

	@Override
	public float getProsperity() {
		float prosperity = (cover.getProsperity()+rew.getProsperity())/2;
		
		System.out.println("observed: entire value: "+prosperity
		+"  That is cover: "+cover.getProsperity()+
				" reward/step: "+rew.getProsperity());
		return prosperity;
	}

	@Override
	public void softReset(boolean randomize) {
		cover.softReset(randomize);
		rew.softReset(randomize);
	}

	@Override
	public void hardReset(boolean randomize) {
		this.softReset(randomize);
	}

}
