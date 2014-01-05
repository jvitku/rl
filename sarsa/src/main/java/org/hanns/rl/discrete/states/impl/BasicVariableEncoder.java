package org.hanns.rl.discrete.states.impl;

import org.hanns.rl.discrete.states.VariableEncoder;

/**
 * Basic state variable encoder which samples specified range of input data 
 * into specified number of samples. No need to make new instance for each variable. 
 *  
 * @author Jaroslav Vitku
 *
 */
public class BasicVariableEncoder implements VariableEncoder{

	private final int numValues;
	private final double from, to;

	private final double step;

	/**
	 * This sets up parameters of the variable encoder, which should then encode
	 * raw values into sampled integer values from the finite domain.
	 * 
	 * @param rangeFrom lowest encoded raw value
	 * @param rangeTo highest encoded raw value
	 * @param noSamples number of samples (determines how fine the sampling step will be)
	 */
	public BasicVariableEncoder(double rangeFrom, double rangeTo, int noSamples){

		this.numValues = noSamples;

		if(rangeTo == rangeFrom){
			System.err.println("BasicVariableEncoder: error, incorrect range!" +
					"Will return only one value!");
			from = rangeTo;
			to = rangeTo;
			step = 0;
			return;
		}

		if(rangeTo < rangeFrom){
			System.err.println("BasicVariableEncoder: warning, incorrect range!");
			from = rangeTo;
			to = rangeFrom;
		}else{
			from = rangeFrom;
			to = rangeTo;
		}
		// this should define the sampling ranges
		step = (to-from)/numValues; 
	}

	@Override
	public int decode(final float raww){

		if(raww>=to)
			return this.numValues-1;	// max value

		if(raww<=from)
			return 0;					// min value

		double val = from;
		int result = 0;

		while((val+step) <= raww){	// count steps until the value is overshot
			val += step;
			result++;
		}
		return result;
	}
	
	
	@Override
	public int getNumValues() { return this.numValues; }

	@Override
	public float encode(int value) {
		return (float) (from+value*step);
	}

	@Override
	public double getMinRaw() { return from; }

	@Override
	public double getMaxRaw() {return to; }


}
