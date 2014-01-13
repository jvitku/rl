package org.hanns.rl.discrete.observer.stats.impl;


/**
 * The same as {@link BinaryCoverageReward}, but here, the {@linkBinaryCoverageForgetting} is used,
 * this should provide more informative results about agents behaviour.
 * 
 * @author Jaroslav Vitku
 * 
 * @see ctu.nengoros.nodes.HannsNode
 */
public class BinaryCoverageForgettingReward extends BinaryCoverageReward{
	
	public static final String name = "BinaryCoverageForgettingReward";
	public static final String explanation = "Value from [0,1]." +
			"Combines BinaryConverageForgettng and Reward/Step values 50/50.";
	
	public BinaryCoverageForgettingReward(int[] varSizes){
		super(varSizes);
		
		// redefine the cover observer
		cover = new BinaryCoverageForgetting(varSizes);
	}
}
