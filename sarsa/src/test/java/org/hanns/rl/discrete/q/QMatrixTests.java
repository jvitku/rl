package org.hanns.rl.discrete.q;

import static org.junit.Assert.*;

import ctu.nengoros.util.SystemInfo;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.impl.BasicFinalQMatrix;
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
		int numVars = 5;	// 5 state variables
		int vs = 10; 
		int[] stateSizes = new int[numVars];
		
		for(int i=0; i<numVars; i++){
			stateSizes[i] = vs;
		}
		System.out.println("============== starting to allocate the structure");
		SystemInfo.infoMb();
		

		BasicFinalQMatrix q = new BasicFinalQMatrix(stateSizes, numActions);
		System.out.println("============== structure allocated, stats:");
		SystemInfo.infoMb();
		System.out.println("");
		
		
		double def = q.getDefaultValue();
		
		int [] coords = this.initCoords(numVars);
		assertTrue(q.get(coords)==def);
		
		coords[1] = 1;	// accesses {0,1,0,...0}
		q.set(coords, -12.3);
		assertTrue(q.get(coords)==-12.3);
		
		// delete all values and try again
		q.hardReset(false);
		assertTrue(q.get(coords)==def);
		
		coords[3] = 6;
		
		assertTrue(def == q.getDefaultValue());
		assertTrue(q.get(coords)==def);
		assertTrue(q.get(new int[]{1,1,1})==null);
		
		coords[5] = 5;						// out of range for actions
		assertTrue(q.get(coords)==null);
		q.get(coords);						// just prints out an error
		coords[5] = 4;						// in range
		assertTrue(q.get(coords)==def);
		coords[4] = 9;						// in range
		assertTrue(q.get(coords)==def);
		coords[4] = 10;						// out of range for state var.
		assertTrue(q.get(coords)==null);
	}
	
	/**
	 * Check randomization
	 */
	public void hardResetRandomize(){
		int numActions = 5;	// 5 actions
		int numVars = 5;	// 5 state variables
		int vs = 10; 		// 	.. each with 10 values
		int[] stateSizes = new int[numVars];
		
		for(int i=0; i<numVars; i++){
			stateSizes[i] = vs;
		}
		System.out.println("============== starting to allocate the structure");
		SystemInfo.infoMb();
		

		BasicFinalQMatrix q = new BasicFinalQMatrix(stateSizes, numActions);
		System.out.println("============== structure allocated, stats:");
		SystemInfo.infoMb();
		System.out.println("");
		
		double def = q.getDefaultValue();
		double []r =q.getRandomizeRange();
		
		int [] coords = this.initCoords(numVars);
		assertTrue(q.get(coords)==def);
		
		coords[1] = 1;	// accesses {0,1,0,...0}
		q.set(coords, -12.3);
		assertTrue(q.get(coords)==-12.3);
		
		q.hardReset(true);
		assertTrue(this.inRange(r,q.get(coords)));
		
		coords[0] = 1;
		assertTrue(this.inRange(r,q.get(coords)));
		
		coords[5] = 2;
		assertTrue(this.inRange(r,q.get(coords)));
		assertFalse(q.get(coords)==def);
	}
	
	private boolean inRange(double [] range, double d){
		return d>=range[0] && d<(range[0]+range[1]);
	}

	
	private int[] initCoords(int numVars){
		int []coords = new int[numVars+1];
		for(int i=0; i<numVars+1; i++){
			coords[i] = 0;
		}
		return coords;
	}


}
