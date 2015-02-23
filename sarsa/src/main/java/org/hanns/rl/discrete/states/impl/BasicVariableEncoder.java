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

	public static final double DEF_TOL = 0.00001;
	
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

		while( this.toleranceLessEqual((val+step),raww, DEF_TOL)){	
		//while((val+step) <= raww){	// count steps until the value is overshot (did not work)
			val += step;
			result++;
		}
		if(result > numValues-1)
			result = numValues-1;
		return result;
	}
	
	/**
	 * This operation is similar to <=, but this operation 
	 * is computed with a given tolerance (e.g. 7 <= 0.69999 is true)
	 * Equation computed is therefore return: 
	 * (leq <= what+tolerance) || (leq <=what-tolerance)
	 * 
	 * @param leq value that is tested to be less or equal
	 * @param what value to be leq compared with
	 * @param tolerance tolerance of operation
	 * @return true if the equation is true
	 */
	private boolean toleranceLessEqual(double leq, double what, double tolerance){
		
		return(leq <= what+tolerance || leq <=what-tolerance);
		
	}
	
	@Override
	public int getNumValues() { return this.numValues; }

	@Override
	public float encode(int value) {
		if(value < 0)
			return (float) from;
		
		//double vl = value; // this makes difference
		int vl = value;
		
		float val = (float) (from+vl*step);
		if(val > to)
			return (float) to;
		return val;
	}

	@Override
	public double getMinRaw() { return from; }

	@Override
	public double getMaxRaw() {return to; }

}
