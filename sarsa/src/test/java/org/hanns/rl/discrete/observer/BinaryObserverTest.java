package org.hanns.rl.discrete.observer;

import static org.junit.Assert.*;

import org.hanns.rl.discrete.observer.impl.BinaryCoverage;
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
		assertTrue(bc.getProsperity()==1/6);	// that is 1/6 of states
		
		bc.observe(-134, -12, new int[]{1,2}, -2);	// visit the same 2x
		assertTrue(bc.getNoVisitedStates()==1);	
		assertTrue(bc.getProsperity()==1/6);	
		
		bc.observe(-134, -12, new int[]{1,1}, -2);
		assertTrue(bc.getNoVisitedStates()==2);	
		assertTrue(bc.getProsperity()==2/6);	
		
		bc.observe(-134, -12, new int[]{1,0}, -2);
		assertTrue(bc.getNoVisitedStates()==3);	
		assertTrue(bc.getProsperity()==3/6);	
		
		bc.observe(-134, -12, new int[]{0,0}, -2);
		assertTrue(bc.getNoVisitedStates()==4);	
		assertTrue(bc.getProsperity()==4/6);
		
		bc.observe(-134, -12, new int[]{0,1}, -2);
		assertTrue(bc.getNoVisitedStates()==5);	
		assertTrue(bc.getProsperity()==5/6);
		
		bc.observe(-134, -12, new int[]{0,2}, -2);
		assertTrue(bc.getNoVisitedStates()==6);	
		assertTrue(bc.getProsperity()==6/6);	
	}

}
