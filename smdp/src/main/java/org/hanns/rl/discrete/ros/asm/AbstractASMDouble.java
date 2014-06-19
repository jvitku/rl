package org.hanns.rl.discrete.ros.asm;

import ctu.nengoros.util.SL;

/**
 * {@link AbstractASM} which selects over the vector of utility scalars of type Double.
 * 
 * @author Jaroslav Vitku
 *
 */
public abstract class AbstractASMDouble extends AbstractASM{
	
	protected Double[] tmpData;
	
	@Override
	public boolean isStarted(){
		if(!super.isStarted())
			return false;
		if(asm == null)
			return false;
		return true;
	}

	@Override
	protected float[] selectActionAndEncode(float[] data) {

		tmpData = new Double[data.length];
		for(int i=0; i<data.length; i++)
			tmpData[i] = (double) data[i];

		int selected = asm.selectAction(tmpData);
		
		// encode the action with 1ofN
		float[] send = actionEncoder.encode(selected); 
		log.info("Encoding this vector: "+SL.toStr(data)+" into this vector: "+send);
		return send;
	}
}
