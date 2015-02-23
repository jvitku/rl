package org.hanns.rl.discrete.states;

import static org.junit.Assert.*;

import org.hanns.rl.discrete.states.impl.BasicFinalStateSet;
import org.hanns.rl.discrete.states.impl.BasicStateVariable;
import org.hanns.rl.discrete.states.impl.BasicVariableEncoder;
import org.hanns.rl.common.exceptions.MessageFormatException;
import org.junit.Test;

/**
 * Test the BasicFinalStateSet, add variables, set raw data, read encoded values.
 * 
 * @author Jaroslav Vitku
 *
 */
public class BasicFinalStateSetTest {

	//@Ignore
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

	/**
	 * State 	-> raw value 
	 * raw value-> state
	 * 
	 * TODO this DOES NOT WORK
	 */
	//@Ignore
	@Test
	public void communicationChannel(){
		int vars = 10;
		VariableEncoder enc = new BasicVariableEncoder(0,1,vars);
		BasicStateVariable v = new BasicStateVariable("x",enc);
		BasicStateVariable vv = new BasicStateVariable("y",enc);

		for(int i=0; i<vars; i++){
			v.setRawValue(enc.encode(i));

			for(int j=0; j<vars; j++){
				vv.setRawValue(enc.encode(j));

				sendReceive(v, i, vv,j);
			}
		}
	}

	/**
	 * Send two variables (read, encode to raw), receive variables (decode 
	 * from raw, read) and compare the sent and received values.
	 * @param a x axis
	 * @param val value that has been encoded
	 * @param b y axis
	 * @param vall value that has been encoded by b
	 */
	private void sendReceive(BasicStateVariable a, int val, BasicStateVariable b, int vall){
		//System.out.println("variable a should contain: "+val+" and contains: "
		//		+a.getVal()+" raw: "+a.getRawValue());
		assertEquals(a.getVal(),val);
		//System.out.println("variable b should contain: "+vall+" and contains: "
		//		+b.getVal()+" raw: "+b.getRawValue());
		assertEquals(b.getVal(),vall);
	}

	//@Ignore
	@Test
	public void setVsEnc(){
		int vars = 10;
		VariableEncoder enc = new BasicVariableEncoder(0,1,vars);
		BasicStateVariable v = new BasicStateVariable("x", enc);
		for(int i=0; i<vars; i++){
			v.setVal(i);								
			//System.out.println("value: "+i+" set is raw: "+v.getRawValue()+
			//" encoded is "+enc.encode(i));
			assertTrue(v.getRawValue() == enc.encode(i));
		}
	}

	//@Ignore
	@Test
	public void channelII(){
		int vars = 10;
		VariableEncoder enc = new BasicVariableEncoder(0,1,vars);
		BasicStateVariable v = new BasicStateVariable("x", enc);
		BasicStateVariable vv = new BasicStateVariable("y", enc);

		for(int i=0; i<vars; i++){

			v.setVal(i);								// TODO this does work
			vv.setRawValue(enc.encode(i));				// TODO this does not work

			/*
			System.out.println("value: "+i+" set is raw: "+v.getRawValue()+
					" encoded is "+enc.encode(i));

			System.out.println("value: "+i+" set is raw: "+(double)v.getRawValue()+
					"="+(double)vv.getRawValue());

			System.out.println("value: "+i+" set is raw: "+v.getVal()+
					"="+vv.getVal());
			 */
			assertTrue(vv.getRawValue()==v.getRawValue());
		}

	}

	/**
	 * Encode decode test, the value ran through the communication channel should
	 * return unchanged, try it.
	 */
	@Test
	public void testEncodeDecode(){
		int vars = 10;
		VariableEncoder enc = new BasicVariableEncoder(0,1,vars);

		for(int i=0; i<vars; i++){
			System.out.println(i+" encode decode "+enc.decode(enc.encode(i))
					+" raw: "+(double)enc.encode(i));

			assertTrue(i == enc.decode(enc.encode(i)));
		}
	}


}
