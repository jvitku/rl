package org.hanns.rl.discrete.observer.stats.combined;

import org.hanns.rl.discrete.observer.AbsSardaProspObserver;
import org.hanns.rl.discrete.observer.SarsaProsperityObserver;
import org.hanns.rl.discrete.observer.stats.impl.BinaryCoverage;
import org.hanns.rl.discrete.observer.stats.impl.MCR;

import ctu.nengoros.network.node.observer.stats.ProsperityObserver;


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
public class BinaryCoverageReward extends AbsSardaProspObserver{

	public final String name = "BinaryCoverageReward";
	public final String me = "["+name+"] ";
	
	public final String explanation = "Returns values of the " +
			"BinaryCoverage and Reward/Step weighted 50/50";
	
	protected SarsaProsperityObserver cover;
	protected SarsaProsperityObserver rew;

	public BinaryCoverageReward(int[] varSizes){
		cover = new BinaryCoverage(varSizes);
		rew = new MCR();
	}

	@Override
	public void observe(int prevAction, float reward, int[] currentState,
			int futureAction) {

		step++;

		cover.observe(prevAction, reward, currentState, futureAction);
		rew.observe(prevAction, reward, currentState, futureAction);

		if(this.shouldVis && step%this.visPeriod==0){
			this.log();
		}
	}
	
	protected void log(){
		System.out.println(me+"step: "+step+" Observer val: "+
				this.computeProsperity() +
				"  That is cover: "+cover.getProsperity()+
				" reward/step: "+rew.getProsperity());
	}

	protected float computeProsperity(){
		
		float result = (float)(((double)cover.getProsperity()+(double)rew.getProsperity())/2.0);
				
		return result;
	}

	@Override
	public float getProsperity() { return this.computeProsperity(); }

	@Override
	public ProsperityObserver[] getChilds(){
		return new ProsperityObserver[]{
				(ProsperityObserver)cover,
				(ProsperityObserver)rew};
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
