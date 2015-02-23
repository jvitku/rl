package org.hanns.rl.discrete.actions;

import static org.junit.Assert.*;

import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.actions.impl.OneOfNEncoder;
import org.hanns.rl.common.exceptions.DecoderException;
import org.hanns.rl.common.exceptions.FinalParamException;
import org.junit.Test;

public class EncoderAndActionSet {

	@Test
	public void basicFinalActionSet(){

		ActionSetInt a = new BasicFinalActionSet(new String[]{"a","b"});

		assertTrue(a.getNumOfActions() == 2);

		try {
			a.setNoActions(100);
			fail();
		} catch (FinalParamException e) {}
	}

	@Test
	public void oneOfNEncoderEncode(){
		ActionSetInt a = new BasicFinalActionSet(new String[]{"a","b"});
		ActionEncoder ae = new OneOfNEncoder(a);

		float[] ff = ae.encode(1);	// action 1
		assertTrue(ff[0]==OneOfNEncoder.nonselected && ff[1]==OneOfNEncoder.selected);

		ff = ae.encode(0);			// action 0
		assertTrue(ff[0]==OneOfNEncoder.selected && ff[1]==OneOfNEncoder.nonselected);

		ff = ae.encode(-1); 		// no action
		assertTrue(ff[0]==OneOfNEncoder.nonselected && ff[1]==OneOfNEncoder.nonselected);

		ff = ae.encode(-2);			// incorrect
		assertTrue(ff[0]==OneOfNEncoder.nonselected && ff[1]==OneOfNEncoder.nonselected);

		ff = ae.encode(2);  		// out of range
		assertTrue(ff[0]==OneOfNEncoder.nonselected && ff[1]==OneOfNEncoder.nonselected);
	}

	@Test
	public void oneOfNEncoderDecode(){
		ActionSetInt a = new BasicFinalActionSet(new String[]{"a","b"});
		ActionEncoder ae = new OneOfNEncoder(a);

		// one selected
		float[] message = new float[]{OneOfNEncoder.nonselected,OneOfNEncoder.selected};
		int index = -14;
		try {
			index = ae.decode(message);
			assertTrue(index==1);
		} catch (DecoderException e) { fail(); }

		// one selected
		message = new float[]{OneOfNEncoder.selected,OneOfNEncoder.nonselected};
		index = -14;
		try {
			index = ae.decode(message);
			assertTrue(index==0);
		} catch (DecoderException e) { fail(); }

		// none selected
		message = new float[]{OneOfNEncoder.nonselected,OneOfNEncoder.nonselected};
		index = -14;
		try {
			index = ae.decode(message);
			assertTrue(index == -1);
		} catch (DecoderException e) { fail(); }

		message = new float[]{OneOfNEncoder.nonselected,OneOfNEncoder.nonselected,
				OneOfNEncoder.selected};
		index = -14;
		try {
			index = ae.decode(message);
			fail();
		} catch (DecoderException e) { }
	}
}
