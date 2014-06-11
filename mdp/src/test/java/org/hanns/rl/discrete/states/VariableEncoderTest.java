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
		
		assertTrue(enc.decode(-10)==0);		// check out of range
		assertTrue(enc.decode(100)==9);
		
		assertTrue(enc.decode(0f)==0);	// boundaries
		assertTrue(enc.encode(0)==0f);
		assertTrue(enc.decode(1f)==9);
		assertTrue(enc.encode(9)==0.9f);
		
		assertTrue(enc.decode((float)0.001)==0);	// other values
		assertTrue(enc.decode((float)0.099)==0);
		assertTrue(enc.decode((float)0.1)==1);
		assertTrue(enc.encode(1)==0.1f);
		assertTrue(enc.decode((float)0.11)==1);
		
		assertTrue(enc.decode((float)0.1999)==1);
		assertTrue(enc.decode((float)0.2)==2);
		assertTrue(enc.encode(2)==0.2f);
		
		assertTrue(enc.decode((float)0.89)==8);
		assertTrue(enc.encode(8)==0.8f);
		assertTrue(enc.decode((float)0.9)==9);	
		
		assertTrue(enc.decode((float)0.91)==9);
		assertTrue(enc.decode((float)0.99)==9);
		assertTrue(enc.decode((float)1)==9);
		
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