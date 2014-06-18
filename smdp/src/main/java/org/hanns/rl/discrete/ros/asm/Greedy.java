package org.hanns.rl.discrete.ros.asm;

import org.ros.node.ConnectedNode;

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
public class Greedy extends AbstractASM{

	public static final String name = "GreedyASM";
	

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
	
	@Override
	public boolean isStarted(){
		if(!super.isStarted())
			return false;
		/*//TODO
		if(filter==null)
			return false;
			*/
		return true;
	}

	@Override
	protected void registerProsperityObserver() {
		// TODO prosperity of the ASM?
	}

	@Override
	protected float[] selectActionAndEncode(float[] data) {
		float val = data[0];
		int ind = 0;
		
		for(int i=1; i<data.length; i++){
			if(val<data[i]){
				val = data[i];
				ind = i;
			}
		}
		// encode the action with 1ofN
		float[] send = actionEncoder.encode(ind); 
		log.info("Encoding this vector: "+SL.toStr(data)+" into this vector: "+send);
		return send;
	}

	/**
	 * nothing to configure here
	 */
	@Override
	protected void buildASMSumbscribers(ConnectedNode connectedNode) {}
}



