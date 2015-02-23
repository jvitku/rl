package org.hanns.rl.discrete.observer.stats.combined;

import org.hanns.rl.discrete.observer.stats.impl.BinaryCoverageForgetting;


/**
 * The same as {@link BinaryCoverageReward}, but here, the {@linkBinaryCoverageForgetting} is used,
 * this should provide more informative results about agents behaviour.
 * 
 * @author Jaroslav Vitku
 * 
 * @see ctu.nengoros.nodes.HannsNode
 */
public class BinaryCoverageForgettingReward extends BinaryCoverageReward{

	public final String name = "BinaryCoverageForgettingReward";
	public final String me = "["+name+"] ";

	public final String explanation = "Value from [0,1]." +
			"Combines BinaryConverageForgettng and Reward/Step values 50/50.";

	public BinaryCoverageForgettingReward(int[] varSizes){
		super(varSizes);

		// redefine the cover observer
		cover = new BinaryCoverageForgetting(varSizes);
	}

	@Override
	protected void log(){
		System.out.println(me+"step: "+step+" Observer val: "+
				super.computeProsperity() +
				"  That is cover: "+cover.getProsperity()+
				" reward/step: "+rew.getProsperity());
	}

	@Override
	public String getName() { return name;	}

	@Override
	public String getDescription() { return explanation;	}
}
