package org.hanns.rl.discrete.states.impl;

import org.hanns.rl.discrete.states.StateVariable;
import org.hanns.rl.discrete.states.VariableEncoder;

/**
 * Basic StateVariable which uses own VariableEncoder to encode 
 * raw input values (float) into integer values used by the discrete 
 * algorithms.
 *  
 * @author Jaroslav Vitku
 *
 */
public class BasicStateVariable implements StateVariable{

	private final String name;
	private final VariableEncoder enc;

	private float rawval;	// value actually received
	private int val;		// value encoded

	public BasicStateVariable(String name, VariableEncoder encoder){
		this.name = name;
		this.enc = encoder;
		this.rawval = 0;
		this.val = 0;
	}

	@Override
	public String getName() { return this.name; }

	@Override
	public int getVal() { return this.val; }

	@Override
	public void setRawValue(float value) { 
		this.rawval = value;
		this.val = enc.encode(value);
	}

	@Override
	public float getRawValue() { return this.rawval; }

	@Override
	public int getNumValues() { return enc.getNumValues(); }

}
