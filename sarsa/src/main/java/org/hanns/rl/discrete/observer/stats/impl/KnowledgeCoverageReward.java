package org.hanns.rl.discrete.observer.stats.impl;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.observer.stats.AbsProsperityObserver;
import org.hanns.rl.discrete.observer.stats.ProsperityObserver;

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
public class KnowledgeCoverageReward extends AbsProsperityObserver{

	public final String name = "KnowledgeCoverageReward";
	public final String explanation = "Value from [0,1] combining" +
			" KnowledgeCoverage and Reward/Step values 50/50.";
	
	KnowledgeCoverage cover;
	BinaryRewardPerStep rew;

	public KnowledgeCoverageReward(int[] varSizes, FinalQMatrix<Double> q){
		cover = new KnowledgeCoverage(varSizes,q);
		rew = new BinaryRewardPerStep();
	}

	@Override
	public void observe(int prevAction, float reward, int[] currentState,
			int futureAction) {

		step++;

		cover.observe(prevAction, reward, currentState, futureAction);
		rew.observe(prevAction, reward, currentState, futureAction);

		if(this.shouldVis && this.step%this.visPeriod==0)
			System.out.println("Step:"+this.step+"observed: entire value: "
					+this.getProsperity()
					+"  That is cover: "+cover.getProsperity()
					+" reward/step: "+rew.getProsperity());
	}

	@Override
	public float getProsperity() {
		return (cover.getProsperity()+rew.getProsperity())/2;
	}

	@Override
	public ProsperityObserver[] getChilds(){
		return new ProsperityObserver[]{cover,rew};
	}

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
		cover.softReset(randomize);
		rew.softReset(randomize);
	}
	
	@Override
	public String getName() { return name;	}

	@Override
	public String getDescription() { return explanation;	}

}