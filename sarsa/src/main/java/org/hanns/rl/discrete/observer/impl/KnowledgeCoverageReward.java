package org.hanns.rl.discrete.observer.impl;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.observer.Observer;

/**
 * The same as {@link org.hanns.rl.discrete.observer.impl.BinaryCoverageReward}, but instead
 * of BinaryCoverage, the KnowledgeCoverage is used.
 * 
 * The {@link KnowledgeCoverage} and the {@link BinaryRewardPerStep} are weighted 50 to 50.
 * 
 * @author Jaroslav Vitku
 * 
 * @see ctu.nengoros.nodes.HannsNode
 */
public class KnowledgeCoverageReward implements Observer{

	KnowledgeCoverage cover;
	BinaryRewardPerStep rew;
	
	public KnowledgeCoverageReward(int[] varSizes, FinalQMatrix<Double> q){
		cover = new KnowledgeCoverage(varSizes,q);
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
		/*
		System.out.println("observed: entire value: "+prosperity
		+"  That is cover: "+cover.getProsperity()+
				" reward/step: "+rew.getProsperity());
				*/
		return prosperity;
	}
	
	@Override
	public Observer[] getChilds(){
		return new Observer[]{cover,rew};
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
