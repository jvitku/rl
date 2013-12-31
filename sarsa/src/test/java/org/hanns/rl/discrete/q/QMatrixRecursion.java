package org.hanns.rl.discrete.q;

import org.hanns.rl.discrete.learningAlgorithm.qlearning.model.impl.Dimension;
import org.junit.Test;

public class QMatrixRecursion {
	
	@Test
	public void init(){
		
		int []dims = new int[]{2,3,4};
		
		Dimension dd = new Dimension(dims,0,-2);
		
		System.out.println("XXXXXXXXXXXXXX");
		
		
		int[] coords = new int[]{0,2,3};
		dd.setValue(coords, 7);
	
		System.out.println("XXXXXXXXXXXXXX");
		
		System.out.println(dd.readValue(coords));
		
	}

}
