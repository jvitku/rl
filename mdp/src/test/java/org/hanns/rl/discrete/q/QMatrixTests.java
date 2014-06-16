package org.hanns.rl.discrete.q;

import static org.junit.Assert.*;

import ctu.nengoros.util.SystemInfo;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.impl.PreAllocatedFinalQMatrix;
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

		PreAllocatedFinalQMatrix q = new PreAllocatedFinalQMatrix(stateSizes, numActions);

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
		
		q.hardReset(false);
		int[] dimsizes = q.getDimensionSizes();
		int[] last = new int[]{dimsizes[0]-1, dimsizes[1]-1, dimsizes[2]-1};
		
		this.check(q, last, 11.11, def);
		last[0] = 0;
		this.check(q, last, 11.11, def);
		last[1] = 0;
		this.check(q, last, 11.11, def);
		last[2] = 0;
		this.check(q, last, 11.11, def);

		
		last = new int[]{dimsizes[0]-1, dimsizes[1]-1, dimsizes[2]-1};
		last[1] = 0;
		this.check(q, last, 11.11, def);
		last = new int[]{dimsizes[0]-1, dimsizes[1]-1, dimsizes[2]-1};
		last[2] = 0;
		this.check(q, last, 11.11, def);
	}
	
	private void check(PreAllocatedFinalQMatrix q, int[] coords, double val, double def){
		assertTrue(q.get(coords)==def);
		q.set(coords, val);
		assertTrue(q.get(coords)==val);
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


		PreAllocatedFinalQMatrix q = new PreAllocatedFinalQMatrix(stateSizes, numActions);
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
		System.err.println("------ start printing out errors..");
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
		System.err.println("------ printing out errors done..");
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


		PreAllocatedFinalQMatrix q = new PreAllocatedFinalQMatrix(stateSizes, numActions);
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


	/**
	 * Write action values by means of coords array, read them
	 */
	@Test
	public void testGetActionValues(){

		// this allocates half a million  of state-action values = 5*10^5
		int numActions = 5;	// 5 actions
		int numVars = 5;	// 5 state variables
		int vs = 10; 		// 	.. each with 10 values
		int[] stateSizes = new int[numVars];

		for(int i=0; i<numVars; i++){
			stateSizes[i] = vs;
		}
		PreAllocatedFinalQMatrix q = new PreAllocatedFinalQMatrix(stateSizes, numActions);
		double def = q.getDefaultValue();
		int [] coords = this.initCoords(numVars);

		assertTrue(q.get(coords)==def);
		coords[0] = 1;	// set some random state
		coords[1] = 2;
		coords[3] = 9;
		coords[4] = 0;

		double[] actionVals = new double[]{-17.0,1.0,2.0,99.0,11.0};
		coords[coords.length-1] = 0;	// set value for action 0 
		q.set(coords, actionVals[0]);
		coords[coords.length-1] = 1;	 
		q.set(coords, actionVals[1]);
		coords[coords.length-1] = 2;
		q.set(coords, actionVals[2]);
		coords[coords.length-1] = 3;
		q.set(coords, actionVals[3]);
		coords[coords.length-1] = 4;
		q.set(coords, actionVals[4]);

		// get coordinates of the sate that was used above
		int[] state = new int[numVars];
		for(int i=0; i<state.length; i++)
			state[i] = coords[i];

		Double[] actionValsRead = q.getActionValsInState(state);

		assertTrue(actionValsRead.length == actionVals.length);

		// values read are the same as those written?
		for(int i=0; i<actionVals.length; i++)
			assertTrue(actionValsRead[i] == actionVals[i]);
		
		coords = this.initCoords(numVars);	// [0,0,0,...]
		assertTrue(q.get(coords)==def);
	}

	/**
	 * use the set method for the same setting of a value into the matrix
	 */
	public void setGetActionValues(){

		// this allocates half a million  of state-action values = 5*10^5
		int numActions = 5;	// 5 actions
		int numVars = 5;	// 5 state variables
		int vs = 10; 		// 	.. each with 10 values
		int[] stateSizes = new int[numVars];

		for(int i=0; i<numVars; i++){
			stateSizes[i] = vs;
		}
		PreAllocatedFinalQMatrix q = new PreAllocatedFinalQMatrix(stateSizes, numActions);
		double def = q.getDefaultValue();
		int [] coords = this.initCoords(numVars);

		assertTrue(q.get(coords)==def);
		coords[0] = 1;	// set some random state
		coords[1] = 2;
		coords[2] = 7;
		coords[3] = 9;
		coords[4] = 0;

		double[] actionVals = new double[]{-17.0,1.0,2.0,99.0,11.0};
		
		for(int i=0; i<actionVals.length; i++)
			q.set(coords, i, actionVals[i]);
		
		// get coordinates of the sate that was used above
		int[] state = new int[numVars];
		for(int i=0; i<state.length; i++)
			state[i] = coords[i];

		Double[] actionValsRead = q.getActionValsInState(state);

		assertTrue(actionValsRead.length == actionVals.length);

		for(int i=0; i<actionVals.length; i++)
			assertTrue(actionValsRead[i] == actionVals[i]);
	}

	/**
	 * Initialize the array of coordinates with zeros
	 * @param numVars number of state variables
	 * @return array with coordinates
	 */
	private int[] initCoords(int numVars){
		int []coords = new int[numVars+1];
		for(int i=0; i<numVars+1; i++){
			coords[i] = 0;
		}
		return coords;
	}


}
