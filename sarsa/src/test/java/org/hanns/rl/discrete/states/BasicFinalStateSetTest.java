package org.hanns.rl.discrete.states;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import org.hanns.rl.discrete.states.impl.BasicFinalStateSet;
import org.hanns.rl.discrete.states.impl.BasicStateVariable;
import org.hanns.rl.discrete.states.impl.BasicVariableEncoder;
import org.hanns.rl.exceptions.MessageFormatException;
import org.junit.Test;

/**
 * Test the BasicFinalStateSet, add variables, set raw data, read encoded values.
 * 
 * @author Jaroslav Vitku
 *
 */
public class BasicFinalStateSetTest {
	
	@Test
	public void twoVars(){
		VariableEncoder enc = new BasicVariableEncoder(0,1,10);
		VariableEncoder encc = new BasicVariableEncoder(0,1,11);
		BasicStateVariable v = new BasicStateVariable("name",enc);
		BasicStateVariable vv = new BasicStateVariable("name1",encc);
		
		BasicFinalStateSet s = new BasicFinalStateSet(new BasicStateVariable[]{v,vv});
		
		assertTrue(s.getNumVariables()==2);
		
		assertTrue(s.getDimensionsSizes().length ==2);
		
		assertTrue(s.getDimensionsSizes()[0]==10);
		assertTrue(s.getDimensionsSizes()[1]==11);
		
		assertTrue(s.getValueOf(0)==0);
		assertTrue(s.getValueOf("name")==0);
		assertTrue(s.getValueOf(1)==0);
		assertTrue(s.getValueOf("name1")==0);
		
		try {
			s.setRawData(new float[]{(float)0.99,(float)0.99});
			assertTrue(s.getValues()[0]==9);
			assertTrue(s.getValues()[1]==10);
			
			assertTrue(s.getValueOf(0)==s.getVarByName("name").getVal());
			assertTrue(s.getValueOf(1)==s.getVarByName("name1").getVal());
			
		} catch (MessageFormatException e) {
			e.printStackTrace();
			fail();
		}
		
		try {
			s.setRawData(new float[]{(float)0,(float)1000});
			assertTrue(s.getValues()[0]==0);
			assertTrue(s.getValues()[1]==10);
		} catch (MessageFormatException e) {
			fail();
			e.printStackTrace();
		}
		
	}
}
