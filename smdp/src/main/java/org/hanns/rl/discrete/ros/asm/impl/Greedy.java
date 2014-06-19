package org.hanns.rl.discrete.ros.asm.impl;

import org.hanns.rl.discrete.actions.ActionSetInt;
import org.ros.node.ConnectedNode;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.ImportanceEpsGreedyDouble;
import org.hanns.rl.discrete.actionSelectionMethod.greedy.GreedyDouble;
import org.hanns.rl.discrete.ros.asm.AbstractASM;
import org.hanns.rl.discrete.ros.asm.AbstractASMDouble;

import ctu.nengoros.util.SL;

/**
 * 
 * Implements ASM (Action Selection Method) in the ROS node. 
 * This is simple Greedy ASM, which selects action with the highest utility value and passes
 * the resulting data in 1ofN code. 
 * 
 * @see {@link AbstractASM}
 * 
 * @author Jaroslav Vitku
 *
 */
public class Greedy extends AbstractASMDouble{

	public static final String name = "GreedyASM";

	/**
	 * Here it is important
	 */
	@Override
	protected void initializeASM(){
		asm = new GreedyDouble((ActionSetInt)this.actions);
	}
	
	@Override
	protected void registerProsperityObserver() {
		// TODO prosperity of the ASM?
	}

	/**
	 * Nothing to do here
	 */
	@Override
	protected void buildASMSumbscribers(ConnectedNode connectedNode) {}
	
	/**
	 * Instantiate the ProsperityObserver
	 * //TODO the prosperity
	@Override
	protected void registerProsperityObserver(){
		//o = new BinaryCoverageForgettingReward(this.states.getDimensionsSizes());
		//o = new KnowledgeChange(this.states.getDimensionsSizes(), q);
		//o = new ForgettingCoverageChangeReward(this.states.getDimensionsSizes(),q);
		o = new MCR();

		observers.add(o);
	}*/
	
	/*
	@Override
	protected void registerParameters(){
		super.registerParameters();
		paramList.addParam(filterConf, ""+MessageDerivator.DEF_MAXLOOP, "The maximum" +
				" length (in sim. steps) of the closed loop: action->newState");
	}
	*/
	

}



