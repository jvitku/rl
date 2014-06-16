package org.hanns.rl.discrete.nengorosIntegration;

import static org.junit.Assert.*;

import org.hanns.rl.discrete.ros.sarsa.ioHelper.MessageDerivator;
import org.junit.Test;

public class Derivator {
	
	@Test
	public void derive(){
		
		MessageDerivator md = new MessageDerivator(1);
		assertTrue(md.getMaxloopLength()==1);
		
		float[] a = new float[]{0,1,2,3.33f};
		float[] aa = a.clone();
		float[] b = new float[]{0,1,2,3.34f};
		
		assertTrue(md.newMessageShouldBePassed(a));//first message should be passed to the alg.
		
		assertFalse(md.newMessageShouldBePassed(a));	//do not send yet
		assertTrue(md.newMessageShouldBePassed(a));	//send - wait time (1 step) expired
		
		assertFalse(md.newMessageShouldBePassed(a));	//do not send (wait for loop to pass the response)
		assertTrue(md.newMessageShouldBePassed(a));	//send again
		
		assertFalse(md.newMessageShouldBePassed(aa));//identical message, do not send..
		assertTrue(md.newMessageShouldBePassed(aa));	//identical message, send
		
		assertTrue(md.newMessageShouldBePassed(b));	//different message
		assertTrue(md.newMessageShouldBePassed(aa));	//different message
		
		
		///////////////////
		md.setMaxClosedLoopLength(4);	// now, wait for 4 messages
		
		assertTrue(md.newMessageShouldBePassed(a));	//first message 
		
		assertFalse(md.newMessageShouldBePassed(a));	//do not send yet
		assertFalse(md.newMessageShouldBePassed(a));	//do not send yet
		assertFalse(md.newMessageShouldBePassed(a));	//do not send yet
		assertFalse(md.newMessageShouldBePassed(a));	//do not send yet
		
		assertTrue(md.newMessageShouldBePassed(a));	//send already
	}
	
	@Test
	public void reset(){
		
		MessageDerivator md = new MessageDerivator(1);
		assertTrue(md.getMaxloopLength()==1);
		
		float[] a = new float[]{0,1,2,3.33f};
		
		assertTrue(md.newMessageShouldBePassed(a));//first message should be passed to the alg.
		assertFalse(md.newMessageShouldBePassed(a));	//do not send yet
		assertTrue(md.newMessageShouldBePassed(a));	//send - wait time (1 step) expired
		
		md.softReset(false);
		assertTrue(md.newMessageShouldBePassed(a));	//send derivator restarted
		
	}

}
