package org.hanns.rl.discrete.observer.stats.combined;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.observer.AbsSardaProspObserver;
import org.hanns.rl.discrete.observer.stats.impl.BinaryCoverageForgetting;
import org.hanns.rl.discrete.observer.stats.impl.MCR;
import org.hanns.rl.discrete.observer.stats.impl.KnowledgeChange;

import ctu.nengoros.network.node.observer.stats.ProsperityObserver;

/**
 * 
 * Combines these, each by 1/3:
 * 
 * {@link org.hanns.rl.discrete.observer.stats.impl.BinaryCoverageForgetting}
 * {@link org.hanns.rl.discrete.observer.stats.impl.MCR}
 * {@link org.hanns.rl.discrete.observer.stats.impl.KnowledgeChange},
 *  
 * this says that the ideally RL algorithm should:
 * <ul>
 * <li>cover the sate space well and regularly<li>
 * <li>have high reward per step<li>
 * <li>have small change of knowledge (that is not oscillating)<li>
 * </ul>
 * 
 * 
 * @author Jaroslav Vitku
 *
 */
public class ForgettingCoverageChangeReward extends AbsSardaProspObserver{


	public final String name = "ForgettingCoverageChangeReward";
	public final String explanation = "Value from [0,1] combining" +
			" BinaryCoverageForgetting, Reward/Step and 1-KnowledgeChange values, each by 1/3.";
	
	BinaryCoverageForgetting cover;
	MCR rew;
	KnowledgeChange ch;

	public ForgettingCoverageChangeReward(int[] varSizes, FinalQMatrix<Double> q){
		cover = new BinaryCoverageForgetting(varSizes);
		rew = new MCR();
		ch = new KnowledgeChange(varSizes, q);
	}

	@Override
	public void observe(int prevAction, float reward, int[] currentState,
			int futureAction) {

		step++;

		cover.observe(prevAction, reward, currentState, futureAction);
		rew.observe(prevAction, reward, currentState, futureAction);
		ch.observe(prevAction, reward, currentState, futureAction);
		
		if(this.shouldVis && this.step%this.visPeriod==0)
			System.out.println("Step:"+this.step+"observed: entire value: "
					+this.getProsperity()
					+"  That is cover: "+cover.getProsperity()
					+" reward/step: "+rew.getProsperity()
					+" 1-knowledge change per step: "+ch.getProsperity());
	}

	@Override
	public float getProsperity() {
		return (float)(((double)cover.getProsperity()
				+(double)rew.getProsperity()
				+(double)ch.getProsperity())/3.0);
	}

	@Override
	public ProsperityObserver[] getChilds(){
		return new ProsperityObserver[]{cover,rew,ch};
	}

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
		cover.softReset(randomize);
		rew.softReset(randomize);
		ch.softReset(randomize);
	}
	
	@Override
	public String getName() { return name;	}

	@Override
	public String getDescription() { return explanation;	}

}
