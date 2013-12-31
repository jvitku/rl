package org.hanns.rl.discrete.states;

import static org.junit.Assert.*;

import org.hanns.rl.discrete.states.impl.BasicStateVariable;
import org.hanns.rl.discrete.states.impl.BasicVariableEncoder;
import org.junit.Test;

/**
 * Tests the properties of basic state variables and their encoders.
 * 
 * @author Jaroslav Vitku
 *
 */
public class VariableEncoderTest {
	
	@Test
	public void encodingCorrect(){
		
		VariableEncoder enc = new BasicVariableEncoder(0,1,10);
		
		assertTrue(enc.getNumValues() == 10);
		
		assertTrue(enc.encode(-10)==0);		// check out of range
		assertTrue(enc.encode(100)==9);
		
		
		assertTrue(enc.encode((float)0)==0);	// boundaries
		assertTrue(enc.encode((float)1)==9);
		
		assertTrue(enc.encode((float)0.001)==0);	// other values
		assertTrue(enc.encode((float)0.099)==0);
		assertTrue(enc.encode((float)0.1)==1);
		assertTrue(enc.encode((float)0.11)==1);
		
		assertTrue(enc.encode((float)0.1999)==1);
		assertTrue(enc.encode((float)0.2)==2);
		
		assertTrue(enc.encode((float)0.89)==8);
		assertTrue(enc.encode((float)0.9)==8);	// well float
		
		assertTrue(enc.encode((float)0.91)==9);
		assertTrue(enc.encode((float)0.99)==9);
		assertTrue(enc.encode((float)1)==9);
		
	}
	
	@Test
	public void stateVariable(){
		VariableEncoder enc = new BasicVariableEncoder(0,1,10);
		
		StateVariable v = new BasicStateVariable("name",enc);
		
		assertTrue(v.getName().equals("name"));
		assertTrue(v.getVal() == 0);
		
		v.setRawValue((float) 0.1999);
		assertTrue(v.getVal()==1);
		
		v.setRawValue((float) 1000000);
		assertTrue(v.getVal()==9);
		
		v.setRawValue((float) -1000000);
		assertTrue(v.getVal()==0);
	}
	
	

}