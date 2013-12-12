package ctu.hanns.logic.gates;

import static org.junit.Assert.*;

import org.junit.Test;

import ctu.hanns.logic.gates.impl.AND;
import ctu.hanns.logic.gates.impl.NAND;
import ctu.hanns.logic.gates.impl.NOT;
import ctu.hanns.logic.gates.impl.OR;
import ctu.hanns.logic.gates.impl.XOR;
//import junit.framework.TestCase;


/**
 * Test the method solve for all gates.
 * TODO: test their communication abilities over the ROS.
 * 
 * @author j
 *
 */
//public class All extends TestCase {
public class Logic{
	@Test
	public void and(){
		AND a = new AND();
		assertTrue(a.copute(true, true));
		assertFalse(a.copute(true, false));
		assertFalse(a.copute(false, true));
		assertFalse(a.copute(false, false));
	}
	
	@Test
	public void or(){
		OR a = new OR();
		assertTrue(a.copute(true, true));
		assertTrue(a.copute(true, false));
		assertTrue(a.copute(false, true));
		assertFalse(a.copute(false, false));
	}

	@Test
	public void xor(){
		XOR a = new XOR();	
		assertFalse(a.copute(true, true));
		assertTrue(a.copute(true, false));
		assertTrue(a.copute(false, true));
		assertFalse(a.copute(false, false));
	}

	@Test
	public void nand(){
		NAND a = new NAND();	
		assertFalse(a.copute(true, true));
		assertTrue(a.copute(true, false));
		assertTrue(a.copute(false, true));
		assertTrue(a.copute(false, false));
	}
	
	@Test
	public void not(){
		NOT a = new NOT();	
		assertFalse(a.copute(true));
		assertTrue(a.copute(false));
	}
	
	
}
