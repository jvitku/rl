package org.hanns.rl.discrete.actions.impl;

import org.hanns.rl.discrete.actions.ActionEncoder;
import org.hanns.rl.discrete.actions.ActionSet;
import org.hanns.rl.discrete.actions.ActionSetInt;
import org.hanns.rl.common.exceptions.DecoderException;

/**
 * Implements 1ofN encoding. Action is represented by the vector of float values, where 
 * only one value has value of 1, representing the selected (index of) action. 
 * 
 * @author Jaroslav Vitku
 */
public class OneOfNEncoder implements ActionEncoder{

	private final ActionSetInt set;

	public static final float selected = 1;
	public static final float nonselected = 0;

	public OneOfNEncoder(ActionSetInt set){
		this.set = set;
	}

	@Override
	public float[] encode(int no) {
		if(no >= set.getNumOfActions() || no<-1){
			System.err.println("OneOfNEncoder: incorrect index of action!" +
					"Have only this number of actions: "+set.getNumOfActions());
			// set no action and continue
			no= ActionSet.NOOP;
		}
		float[] out = new float[set.getNumOfActions()];
		setNotSelected(out);
		if(no>=0)
			out[no] = selected;
		return out;
	}

	@Override
	public int decode(float[] data) throws DecoderException {
		if(data.length != set.getNumOfActions())
			throw new DecoderException("OneOfNEncoder: message has incorrect length! " +
					"Expected "+set.getNumOfActions()+" and found "+data.length);

		int index = ActionSet.NOOP;	// no action index

		for(int i=0; i<data.length; i++){
			if(data[i] == selected){
				if(index != -1){
					throw new DecoderException("oneOfNEncoder: found more than one" +
							" selected values!");
				}
				index = i;
			}
		}
		return index;
	}

	private void setNotSelected(float[] input){
		for(int i=0; i<input.length; i++)
			input[i] = nonselected;
	}
}
