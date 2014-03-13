package org.hanns.rl.discrete.actionSelectionMethod;

public class AbstractASMConfig {

	/**
	 * Check if a new value to a given parameter is in correct range, if not, warn
	 * and modify the parameter to fit in the range
	 * @param paramName name of the parameter
	 * @param from lower bound inclusive
	 * @param to upper bound inclusive
	 * @param newVal new value to be set
	 * @return modified parameter value if the modification was necessary
	 */
	protected double checkRange(String paramName, double from, double to, double newVal){
		if(!(newVal>=from && newVal<=to)){

			//System.err.println("Config WARNING, the parameter "+paramName+
			//		" should be from the interval ["+from+","+to+"], not "+newVal);
			if(newVal<from)
				return from;
			if(newVal>to)
				return to;
		}
		return newVal;
	}
	
	protected float checkRange(String paramName, float from, float to, float newVal){
		if(!(newVal>=from && newVal<=to)){

			//System.err.println("Config WARNING, the parameter "+paramName+
			//		" should be from the interval ["+from+","+to+"], not "+newVal);
			if(newVal<from)
				return from;
			if(newVal>to)
				return to;
		}
		return newVal;
	}

}
