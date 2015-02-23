package org.hanns.rl.discrete.states.impl;

import java.util.HashMap;

import org.hanns.rl.discrete.states.FInalStateSet;
import org.hanns.rl.discrete.states.StateVariable;
import org.hanns.rl.common.exceptions.MessageFormatException;

/**
 * Unmodifiable set of variables.
 * 
 * @author Jaroslav Vitku
 *
 */
public class BasicFinalStateSet implements FInalStateSet{

	private final BasicStateVariable[] vars;
	private final HashMap<String,StateVariable> map;
	private final int[] dimensionSizes;

	private final int numVars;

	public BasicFinalStateSet(BasicStateVariable[] variables){

		vars = variables.clone();
		numVars = vars.length;

		map = new HashMap<String, StateVariable>(vars.length);
		dimensionSizes = new int[vars.length];

		for(int i=0; i<vars.length; i++){
			if(map.containsKey(vars[i].getName())){
				System.err.println("BasicFInalStateSet: ERROR: cannot add two " +
						"state variables with the same name: "+vars[i].getName()+
						" Values in array and Map would be inconsistent");
				return;
			}else{
				map.put(vars[i].getName(), vars[i]);
				dimensionSizes[i] = vars[i].getNumValues();
			}
		}
	}

	@Override
	public int getNumVariables() { return vars.length; }

	@Override
	public int[] getDimensionsSizes() {
		// prevent modification of data
		return this.dimensionSizes.clone();
	}

	@Override
	public int[] getValues() {
		int[] vals = new int[numVars];
		for(int i=0; i<numVars; i++){
			vals[i] = vars[i].getVal();
		}
		return vals;
	}

	@Override
	public int getValueOf(int index) { return vars[index].getVal(); }

	@Override
	public int getValueOf(String name) {
		if(!map.containsKey(name)){
			System.err.println("BasicFinalStateSet: ERROR: no variable named "+name+" found");
			return -1;
		}
		return map.get(name).getVal();
	}

	@Override
	public StateVariable getVarByName(String name) {
		if(!map.containsKey(name)){
			System.err.println("BasicFinalStateSet: ERROR: no variable named "+name+" found");
			return null;
		}
		return map.get(name);
	}

	@Override
	public void setRawData(float[] values) throws MessageFormatException {
		if(values.length != this.numVars)
			throw new MessageFormatException("BasicFinalStateSet: received array of values " +
					"should have length of: "+this.numVars+" it has "+values.length);

		for(int i=0; i<values.length; i++){
			vars[i].setRawValue(values[i]);
		}
	}
}
