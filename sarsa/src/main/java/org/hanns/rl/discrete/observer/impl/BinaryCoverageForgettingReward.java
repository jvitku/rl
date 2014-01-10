package org.hanns.rl.discrete.observer.impl;


/**
 * The same as {@link BinaryCoverageReward}, but here, the {@linkBinaryCoverageForgetting} is used,
 * this should provide more informative results about agents behaviour.
 * 
 * @author Jaroslav Vitku
 * @see ctu.nengoros.nodes.HannsNode
 */
public class BinaryCoverageForgettingReward extends BinaryCoverageReward{
	
	public BinaryCoverageForgettingReward(int[] varSizes){
		super(varSizes);
		
		// redefine the cover observer
		cover = new BinaryCoverageForgetting(varSizes);
	}

}
