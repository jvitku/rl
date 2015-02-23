package org.hanns.rl.discrete.observer.stats.combined;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.observer.AbsSardaProspObserver;
import org.hanns.rl.discrete.observer.stats.impl.MCR;
import org.hanns.rl.discrete.observer.stats.impl.KnowledgeCoverage;

import ctu.nengoros.network.node.observer.stats.ProsperityObserver;

/**
 * The same as {@link org.hanns.rl.discrete.observer.stats.combined.impl.BinaryCoverageReward}, but instead
 * of BinaryCoverage, the KnowledgeCoverage is used.
 * 
 * The {@link KnowledgeCoverage} and the {@link MCR} are weighted 50 to 50.
 * 
 * @author Jaroslav Vitku
 * 
 * @see ctu.nengoros.nodes.HannsNode
 */
public class KnowledgeCoverageReward extends AbsSardaProspObserver{

	public final String name = "KnowledgeCoverageReward";
	public final String explanation = "Value from [0,1] combining" +
			" KnowledgeCoverage and Reward/Step values 50/50.";
	
	KnowledgeCoverage cover;
	MCR rew;

	public KnowledgeCoverageReward(int[] varSizes, FinalQMatrix<Double> q){
		cover = new KnowledgeCoverage(varSizes,q);
		rew = new MCR();
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
		return (float)(((double)cover.getProsperity()
				+(double)rew.getProsperity())/2.0);
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
