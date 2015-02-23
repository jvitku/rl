package org.hanns.rl.discrete.observer;

import static org.junit.Assert.*;

import org.hanns.rl.discrete.observer.stats.impl.BinaryCoverage;
import org.junit.Test;

/**
 * BinaryConverage should compute how many states has been visited.
 *  
 * @author Jaroslav Vitku
 *
 */
public class BinaryObserverTest {
	
	@Test
	public void computeVisitsSimple(){
		int[] sizes = new int[]{2,3};
		
		BinaryCoverage bc = new BinaryCoverage(sizes);
		assertTrue(bc.getProsperity()==0);
		
		// only the state is important
		bc.observe(-134, -12, new int[]{1,2}, -2);
		
		assertTrue(bc.getNoVisitedStates()==1);	// one state visited
		
		assertTrue(eq(bc.getProsperity(),1f/6f));	// just in case..
		
		assertTrue(bc.getProsperity()==1f/6f);	// that is 1/6 of states
		
		bc.observe(-134, -12, new int[]{1,2}, -2);	// visit the same 2x
		assertTrue(bc.getNoVisitedStates()==1);	
		assertTrue(bc.getProsperity()==1f/6f);	
		
		bc.observe(-134, -12, new int[]{1,1}, -2);
		assertTrue(bc.getNoVisitedStates()==2);	
		assertTrue(bc.getProsperity()==2f/6f);	
		
		bc.observe(-134, -12, new int[]{1,0}, -2);
		assertTrue(bc.getNoVisitedStates()==3);	
		assertTrue(bc.getProsperity()==3f/6f);	
		
		bc.observe(-134, -12, new int[]{0,0}, -2);
		assertTrue(bc.getNoVisitedStates()==4);	
		assertTrue(bc.getProsperity()==4f/6f);
		
		bc.observe(-134, -12, new int[]{0,1}, -2);
		assertTrue(bc.getNoVisitedStates()==5);	
		assertTrue(bc.getProsperity()==5f/6f);
		
		bc.observe(-134, -12, new int[]{0,2}, -2);
		assertTrue(bc.getNoVisitedStates()==6);	
		assertTrue(bc.getProsperity()==6f/6f);
		
		
		// test reset
		bc.hardReset(false);
		assertTrue(bc.getNoVisitedStates()==0);	
		assertTrue(bc.getProsperity()==0);
		
		bc.observe(-134, -12, new int[]{0,2}, -2);
		assertTrue(bc.getNoVisitedStates()==1);	
		assertTrue(bc.getProsperity()==1f/6f);
		
	}
	
	private final double tol = 0.001;
	
	private boolean eq(float a, float b){
		return ((a <= b+tol) && a >= (b-tol));
	}
	

}
