package org.hanns.rl.discrete.visualizaiton.qMatrix;

import org.hanns.rl.common.Resettable;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.visualizaiton.Visualizer;

/**
 * <p>Provides textual visualization of N-dimensional state-space. The dimensions are visualized as
 * follows:
 * <ul>
 * <li>First two dimensions are depicted as a 2D matrix (first~x (indexes increase from left to 
 * right),second~y (indexes increase from the bottom upstairs)), the other dimensions have 
 * indexes set to zeros, so the user can see: {x,y,0,0,0,...0,}.</li>
 * <li>The same x-y matrix is visualized for recursively all other dimensions.</li>
 * <ul> 
 * <p>
 * 
 * <p>The visualizer shows either number of action with the highest utility (from the interval 
 * [0,noActions-1]) or the utility value of the action with the highest utility for a given state.</p>
 * 
 * <p>If the action has not been visited, the state is marked as unvisited by the special symbol.
 * </p> 
 * @author Jaroslav Vitku
 * @param <E>
 *
 */
public abstract class FinalStateSpaceVis<E> implements Visualizer{

	public static final int DEF_DETAILS = 7;
	public static final int DEF_SILENT = 0;

	public static final int DEF_VISPERIOD = 20; // visualize each 20 steps by default

	public static final String NO_ACTION = ".";
	public static final String NO_VALUE = ".";
	public static final String SEPARATOR = "\t";

	private final int[] dimSizes;
	protected final int noActions;
	private final FinalQMatrix<E> q;

	private int visPeriod = DEF_VISPERIOD;			
	private int details = DEF_DETAILS; 

	private int step;
	private int type; // 0 means rounded values, 1 means numbers of actions

	public FinalStateSpaceVis(int[] dimSizes, int noActions, FinalQMatrix<E> q){
		this.dimSizes = dimSizes.clone();
		this.noActions = noActions;
		this.q = q;
		this.type = 0;

		this.softReset(false);
	}


	@Override
	public void performStep(int prevAction, float reward, int[] currentState, int futureAction) {
		if(! (step++ % visPeriod==0 ) )
			return;

		System.out.println("Visalization, step no: "+step+"\n"+this.visualize());

	}

	private String visualize(){
		String out = "";

		DimCounter dc = new DimCounter(dimSizes);
		dc.softReset(false);
		int[] coords = dc.getCurrentCoords();	// get all zeros

		// while there is still something to iterate
		while(true){
			// the y dimension, from the biggest index towards 0 
			for(int j=dimSizes[1]-1; j>0; j--){
				coords[1] = j;
				// the x dimension, from the left to right, from 0 towards end
				for(int i=0; i<dimSizes[0]; i++){
					coords[0] = i;

					// visualize values?
					if(type==0){
						if(!this.foundNonZero(q.getActionValsInState(coords))){
							out = out + SEPARATOR + NO_ACTION;
						}else{
							int ind = this.getMaxActionInd(coords);
							out = out + SEPARATOR + ind;
						}
						// visualize indexes?
					}else{
						if(!this.foundNonZero(q.getActionValsInState(coords))){
							out = out + SEPARATOR + NO_VALUE;
						}else{
							int ind = this.getMaxActionInd(coords);
							out = out + SEPARATOR + q.getActionValsInState(coords)[ind];
						}
					}
				}
			}
			coords = dc.next();
			if(coords==null)
				break;
		}

		return out;
	}


	private int getMaxActionInd(int[] coordinates){
		E[] vals = q.getActionValsInState(coordinates);
		int ind = 0;
		for(int i=1; i<vals.length; i++){
			if(this.better(vals[i],vals[ind])){
				ind = i;
			}
		}
		return ind;
	}

	public void setTypeVisualization(int type){
		if(type == 0 || type ==1){
			this.type = type;
		}else{
			System.err.println("unsupported type of visualization, types supported are: 0/1");
		}
	}

	/**
	 * Return true if in the array of values there was at least one non-zero value
	 * @param values array of action utilities for a given state
	 * @return true if there was at least one non-zero
	 */
	protected abstract boolean foundNonZero(E[] values);
	/**
	 * True if the a is bigger than b
	 * @param a first parameter
	 * @param b second parameter
	 * @return true if the first is bigger (better) than the second one
	 */
	protected abstract boolean better(E a, E b);


	public int getTypeVisualization(){ return this.type; }


	@Override
	public void setVisPeriod(int period) {
		if(period<=0){
			System.err.println("FinalStateSpaceVis: ERROR: will not set negative or zero vis. period");
			period = 1;
		}
		this.visPeriod = period;
	}

	@Override
	public int getVisPeriod() { return this.visPeriod; }

	@Override
	public void setVisDetails(int details) { this.details = details; }

	@Override
	public int getVisDetails() { return this.details; }

	@Override
	public void softReset(boolean randomize) {
		this.step = 0;
	}

	@Override
	public void hardReset(boolean randomize) {
		this.softReset(randomize);
	}


	/**
	 * Setup with dimension sizes. Use the {@link #next()} method
	 * to sequentially iterate across the dimensions from third one
	 * to the last one. After reaching the last index of the last coordinate, 
	 * the null is returned. For restarting the iteration call the {@link #softReset(boolean)}. 
	 * 
	 * @author Jaroslav Vitku
	 *
	 */
	private class DimCounter implements Resettable{

		private final int[] dimSizes;
		private int [] coords;	// current coordinates
		private int currentDim;	// the current dimension that is iterated

		public DimCounter(int[] dimSizes){
			this.dimSizes = dimSizes.clone();

			this.softReset(false);
		}

		/**
		 * Call {@link #softReset(boolean)} before starting to use this.
		 * For obtaining the next unused coordinate (the coordinates of two
		 * first dimensions are ignored).
		 * 
		 * Note: the method is called next, so the first coordinates
		 * returned by this method are: {0,0,1,0,0,...,0}.
		 * 
		 * @return after each call, this method returns unique coordinates 
		 * in the state-space (first two ignored). The coordinates have consecutively 
		 * increasing indexes in increasing dimensions. After reaching the last index 
		 * of the last dimension, the null is returned.   
		 */
		public int[] next(){

			// can add index to this dimension?
			if(coords[currentDim] < dimSizes[currentDim]-1){
				// use the new index, return new coords.
				coords[currentDim]++;
				return coords;
			}else{
				currentDim++;
				// dimension out of range? end this
				if(currentDim==dimSizes.length){
					coords = null;
					return null;
				}
				// not out of range, so return coords.
				return coords;
			}

			//int dim = 2; // ignore first two dims
			/*
			// while the entire dimension is exploited 
			while(coords[dim]==dimSizes[dim]){
				// last dimension and is exploited already? quit.
				if(dim==dimSizes.length-1){
					return null;
				}
				dim++;
			}
			// found first not entirely exploited dimension, increase the index
			coords[dim]++;
			return coords;*/
		}

		public int[] getCurrentCoords(){
			return coords;
		}

		@Override
		public void softReset(boolean randomize) {
			this.coords = new int[dimSizes.length];
			for(int i=0; i<dimSizes.length; i++)
				coords[i] = 0;
			this.currentDim = 2;	// the first dimension to iterate through 
		}

		@Override
		public void hardReset(boolean randomize) { this.softReset(randomize); }
	}

}
