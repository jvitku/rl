package org.hanns.rl.discrete.q;

import static org.junit.Assert.*;

import org.hanns.rl.discrete.learningAlgorithm.qlearning.model.impl.BasicFinalQMatrix;
import org.junit.Test;

/**
 * Test whether the final QMatrix works as expected.
 * 
 * @author Jaroslav Vitku
 *
 */
public class QMatrixTests {
	
	@Test
	public void setGet(){
		
		int numActions = 5;	// 5 actions
		int numVars = 2;	// 2 state variables	
		int[] stateSizes = new int[numVars];
		
		stateSizes[0] = 2;	// var 0 can have two states
		stateSizes[1] = 3;	// var 1 can have three states

		BasicFinalQMatrix q = new BasicFinalQMatrix(stateSizes, numActions);
		
		// this should result in a QMatrix of dimension: 2x3x5
		assertTrue(q.getNumActions()==5);
		assertTrue(q.getNumStateVariables()==2);
		
		// check (expected) dimension sizes of the matrix
		int[] dims = q.getDimensionSizes();
		assertTrue(dims.length == 3);
		assertTrue(dims[0]==2);
		assertTrue(dims[1]==3);
		assertTrue(dims[2]==5);
		
		// set and get
		int []coordinates = new int[]{1,2,4};
		q.set(coordinates, 99.1);
		assertTrue(q.get(coordinates)==99.1);
		
		
		// get some default values
		double def = q.getDefaultValue();
		assertTrue(def==0.0);
		assertTrue(q.get(new int[]{0,0,0})==def);
		assertTrue(q.get(new int[]{1,1,1})==def);
		assertTrue(q.get(new int[]{1,2,3})==def);
		assertFalse(q.get(new int[]{1,2,4})==def);
		
		// delete all values and try again
		q.hardReset(false);
		assertTrue(def == q.getDefaultValue());
		assertTrue(q.get(new int[]{0,0,0})==def);
		assertTrue(q.get(new int[]{1,1,1})==def);
		assertTrue(q.get(new int[]{1,2,3})==def);
		assertTrue(q.get(new int[]{1,2,4})==def);
		
	}

}
