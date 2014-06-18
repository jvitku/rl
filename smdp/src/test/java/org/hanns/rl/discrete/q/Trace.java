package org.hanns.rl.discrete.q;

import static org.junit.Assert.*;

import org.hanns.rl.discrete.learningAlgorithm.lambda.impl.StateTraceImpl;
import org.junit.Test;

public class Trace {
	
	/**
	 * Check the trace implementation for eligibility traces.
	 */
	@Test
	public void trace(){
	
		int n= 3;
		StateTraceImpl t= new StateTraceImpl(n);
		
		assertTrue(t.size()==0);
		int[] one = new int[]{1,2,3};
		t.push(one, 0);
		
		assertTrue(t.get(0).length == 4);
		assertTrue(t.get(1) == null);
		assertTrue(t.size()==1);
		
		assertTrue(ae(new int[]{1,2,3,0}, t.get(0)));
		
		///////////////
		int[] two = new int[]{2,3,4};
		t.push(two, 1);
		
		
		assertTrue(ae(new int[]{2,3,4,1}, t.get(0)));// old moved down
		assertTrue(ae(new int[]{1,2,3,0}, t.get(1)));// new pushed on the top
		assertTrue(t.size() == 2);
		assertTrue(t.getCapacity()==3);
		
		///////////////
		int[] three = new int[]{3,4,5};
		t.push(three, 2);
		
		assertTrue(ae(new int[]{3,4,5,2}, t.get(0)));
		assertTrue(ae(new int[]{2,3,4,1}, t.get(1)));
		assertTrue(ae(new int[]{1,2,3,0}, t.get(2)));// old moved down
		
		assertTrue(t.size() == 3);
		assertTrue(t.getCapacity()==3);
		///////////////

		int[] four = new int[]{4,5,6};
		t.push(four, 3);
		
		assertTrue(ae(new int[]{4,5,6,3}, t.get(0)));
		assertTrue(ae(new int[]{3,4,5,2}, t.get(1)));
		assertTrue(ae(new int[]{2,3,4,1}, t.get(2)));
		assertTrue(t.get(3)==null);// old one deleted (not enough capacity)
		
		assertTrue(t.size() == 3);
		assertTrue(t.getCapacity()==3);
		
	}
	
	private boolean ae(int a[], int[] b){
		if(a.length!=b.length)
			return false;
		for(int i=0; i<a.length; i++){
			if(a[i]!=b[i])
				return false;
		}
		return true;
	}

}
