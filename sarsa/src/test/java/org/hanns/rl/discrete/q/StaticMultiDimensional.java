package org.hanns.rl.discrete.q;

import static org.junit.Assert.*;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.dataStructure.impl.PreAllocatedMultiDimension;
import org.junit.Test;

/**
 * Basic test of QMatrix data structure
 * 
 * @author Jaroslav Vitku
 *
 */
public class StaticMultiDimensional {

	@Test
	public void init(){

		int []dims = new int[]{2,3,4};

		PreAllocatedMultiDimension<Double> dd = new PreAllocatedMultiDimension<Double>(dims,0,new Double(-2));

		int[] coords = new int[]{0,2,3};
		dd.setValue(coords, new Double(7));

		assertTrue(dd.readValue(coords)==7);
		System.out.println("reading value: "+dd.readValue(coords));

		assertTrue(dd.readValue(new int[]{1,1,1})==-2);

	}

}
