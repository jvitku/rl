package org.hanns.rl.discrete.utilityRescaling.impl;

import org.hanns.rl.discrete.utilityRescaling.ImportanceBasedRescaler;

/**
 * Multiplies all action utilities by the current value of action importance:
 * 
 *  <ul>
 *  <li>Small importance -> all utilities near zero</li>
 *  <li>Big importance -> all utilities near its original value</li>
 *  </ul>
 *  
 * @author Jaroslav Vitku
 *
 */
public class DirectUtilityRescale implements ImportanceBasedRescaler{

	private float importance;
	private final int noActions;
	
	private final float[] out;
	
	public DirectUtilityRescale(float importance, int noActions){
		this.importance = importance;
		this.noActions = noActions;
		this.out = new float[noActions]; 
	}

	@Override
	public float getImportance() { return this.importance; }
	
	@Override
	public void setImportance(float importance){
		if(importance>1){
			importance = 1;
		}else if(importance<0){
			importance = 0;
		}
		this.importance = importance;
	}
	
	@Override
	public float[] rescale(float[] utilities) {
		if(utilities.length != this.noActions){
			System.err.println("ERROR: expected number of actions is: "+this.noActions);
			return null;
		}
		for(int i=0; i<utilities.length; i++){
			out[i] = utilities[i]*importance;
		}
		return out;
	}

}
