package org.hanns.rl.discrete.actions;

import static org.junit.Assert.*;

import org.hanns.rl.discrete.actions.impl.ActionBuffer;
import org.junit.Test;

/**
 * Test the correctness of the action buffer.
 * 
 * @author Jaroslav Vitku
 *
 */
public class Buffer {
	
	/**
	 * size 1
	 */
	@Test
	public void buff(){
		
		ActionBufferInt b = new ActionBuffer();
		
		assertTrue(b.getLength()==ActionBuffer.DEF_LEN);
		int def = ActionBuffer.DEF_LEN;
		assertTrue(def == 1); // the following expects it..
		
		b.push(11);
		assertTrue(b.read()==11);
		
		b.push(22);
		assertTrue(b.read()==22);
		
		b.softReset(false);
		assertTrue(b.read()==ActionBuffer.EMPTY);
		
		b.push(-3);
		assertTrue(b.read()==-3);
	}
	
	/**
	 * This buffers one step behind (length of 2).
	 */
	@Test
	public void buffTwo(){
		
		ActionBufferInt b = new ActionBuffer();
		
		assertTrue(b.getLength()==ActionBuffer.DEF_LEN);
		b.setLength(2);
		assertTrue(b.getLength()==2);
		
		
		b.push(11);
		assertTrue(b.read()==11);
		
		b.push(22);
		assertTrue(b.read()==11);
		
		b.push(33);
		assertTrue(b.read()==22);
		
		b.push(44);
		assertTrue(b.read()==33);
		
		
		b.softReset(false);
		assertTrue(b.read()==ActionBuffer.EMPTY);
		
		b.push(-3);
		assertTrue(b.read()==-3);
		
		b.push(3);
		assertTrue(b.read()==-3);
		
		b.push(31);
		assertTrue(b.read()==3);
	}
}
