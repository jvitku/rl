package org.hanns.rl.discrete.q;

import static org.junit.Assert.*;

import ctu.nengoros.util.SystemInfo;

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
		SystemInfo.infoMb();
	}
	
	/**
	 * Create bigger matrix, try to set values, reset it,
	 * read, randomize, read, set, read..
	 */
	@Test
	public void hardReset(){
		
		int numActions = 5;	// 5 actions
		int numVars = 6;	// 2 state variables
		int vs = 10; 
		int[] stateSizes = new int[numVars];
		
		for(int i=0; i<numVars; i++){
			stateSizes[i] = vs;
		}
		SystemInfo.infoMb();
		
		System.out.println("start");
		BasicFinalQMatrix q = new BasicFinalQMatrix(stateSizes, numActions);
		System.out.println("end");
		
		
		SystemInfo.infoMb();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		double def = q.getDefaultValue();
		
		int [] coords = this.initCoors(numVars);
		assertTrue(q.get(coords)==def);
		
		coords[1] = 1;	// accesses {0,5,0,...0}
		q.set(coords, -12.3);
		assertTrue(q.get(coords)==-12.3);
		/*
		// delete all values and try again
		q.hardReset(false);
		
		assertTrue(def == q.getDefaultValue());
		assertTrue(q.get(new int[]{0,0,0})==def);
		assertTrue(q.get(new int[]{1,1,1})==def);
		assertTrue(q.get(new int[]{1,2,3})==def);
		assertTrue(q.get(new int[]{1,2,4})==def);
		*/
	}
	
	private int[] initCoors(int numVars){
		int []coords = new int[numVars+1];
		for(int i=0; i<numVars+1; i++){
			coords[i] = 0;
		}
		return coords;
	}


}
