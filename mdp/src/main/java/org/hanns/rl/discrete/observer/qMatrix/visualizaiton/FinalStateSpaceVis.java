package org.hanns.rl.discrete.observer.qMatrix.visualizaiton;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;

import ctu.nengoros.network.common.Resettable;

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
public abstract class FinalStateSpaceVis<E> implements QMatrixVisualizer{

	public final String name = "FinalStateSpaceVis";
	public final String me = "["+name+"] ";
	
	public static final int DEF_DETAILS = 7;
	public static final int DEF_SILENT = 0;
	
	public static final boolean DEF_SHOULDVIS = false;

	// visualize each 100 steps by default
	public static final int DEF_VISPERIOD = 100; 
	public static final int ROUNDTO = 1000;

	private boolean useRounding = true;
	private boolean shouldVis = DEF_SHOULDVIS;

	public static final String NO_ACTION = " ";
	public static final String NO_VALUE = ".";
	public static final String SEPARATOR = "\t";
	public static final String LINE = "\t------------------------";

	private String[] remaps = null;

	private final int[] dimSizes;
	protected final int noActions;
	private final FinalQMatrix<E> q;

	private int visPeriod = DEF_VISPERIOD;			
	private int details = DEF_DETAILS; 

	private int step;
	private int type; // 0 means numbers of actions, 1 means rounded values

	public FinalStateSpaceVis(int[] dimSizes, int noActions, FinalQMatrix<E> q){
		this.dimSizes = dimSizes.clone();
		this.noActions = noActions;
		this.q = q;
		this.type = 0;

		this.softReset(false);
	}

	@Override
	public void observe(int prevAction, float reward, int[] currentState, int futureAction) {
		if( this.visPeriod < 0 || !this.shouldVis)
			return;
		
		if( step++ % visPeriod != 0 )
			return;

		System.out.println(me+"step no: "+step+"\n"+this.visualize());
	}

	private String visualize(){
		String out = "";

		DimCounter dc = new DimCounter(dimSizes);
		dc.softReset(false);
		int[] coords = dc.getCurrentCoords();	// get all zeros

		// while there is still something to iterate
		while(true){

			out = out + "\n"+LINE+" these dimensions of Q matrix are displayed: "
					+this.writeDims(coords);

			// the y dimension, from the biggest index towards 0 
			for(int j=dimSizes[1]-1; j>=0; j--){
				coords[1] = j;
				out = out + "\n"+j+"\t| ";
				// the x dimension, from the left to right, from 0 towards end
				for(int i=0; i<dimSizes[0]; i++){
					coords[0] = i;
					String sep;
					if(i==0){
						sep="";
					}else
						sep=SEPARATOR;

					// visualize values?
					if(type==0){
						if(!this.foundNonZero(q.getActionValsInState(coords))){
							out = out + sep + NO_ACTION;
						}else{
							int ind = this.getMaxActionInd(coords);
							out = out + sep + ind;
						}
						// visualize indexes?
					}else if(type==1){
						if(!this.foundNonZero(q.getActionValsInState(coords))){
							out = out + sep + NO_VALUE;
						}else{
							int ind = this.getMaxActionInd(coords);
							if(useRounding)
								out = out + sep + this.round(q.getActionValsInState(coords)[ind],ROUNDTO);
							else
								out = out + sep + q.getActionValsInState(coords)[ind];
						}
					}else{
						if(remaps==null){
							System.out.println("visualization: ERROR: for type vis.=2 the remappings "
									+"have to be set!");
							return null;
						}
						if(!this.foundNonZero(q.getActionValsInState(coords))){
							out = out + sep + NO_ACTION;
						}else{
							int ind = this.getMaxActionInd(coords);
							out = out + sep + remaps[ind];
						}
					}
				}
			}
			// append X axis
			out = out+"\nY \t_____";
			for(int i=1; i<dimSizes[0]; i++){
				out = out +SEPARATOR+ "______";
			}
			out = out +"\n   X \t 0";
			for(int i=1; i<dimSizes[0]; i++){
				out = out +SEPARATOR+ i;
			}
			coords = dc.next();
			if(coords==null)
				break;
		}

		//return out+"\n"+LINE+LINE+"\n";
		return out+"\n";
	}

	/**
	 * Round the value to a given number of places
	 * @param what what to round
	 * @param how how big precision 
	 * @return rounded value
	 */
	public abstract E round(E what, int how);

	private String writeDims(int[] dims){
		if(dims.length==1)
			return "[x]";
		if(dims.length==2)
			return "[x,y]";
		String out = "[x,y,";
		for(int i=2; i<dims.length; i++){
			out = out + dims[i];
		}
		return out+"]";
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

	/**
	 * Set the type of visualization
	 * @param type  0 means numbers of actions, 1 means rounded values, 2 uses 
	 * graphical representation if previously set by the {@link #setActionRemapping(String[])}
	 */
	public void setTypeVisualization(int type){
		if(type == 0 || type ==1 || type==2){
			this.type = type;
		}else{
			System.err.println("unsupported type of visualization, types supported are: 0/1/2");
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
		if(period < -1){
			System.err.println("FinalStateSpaceVis: ERROR: period lower than -1, will disable vis.");
			period = -1;
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


	@Override
	public void setRoundingEnabled(boolean enabled) {
		this.useRounding = enabled;
	}


	@Override
	public void setActionRemapping(String[] remaps) {
		if(remaps.length!=this.noActions){
			System.err.println("Visaulization: ERROR: my no actions is: "+this.noActions
					+" not "+remaps.length);
			return;
		}
		this.remaps = remaps;		
	}

	@Override
	public void setShouldVis(boolean visualize) { this.shouldVis = visualize;	}

	@Override
	public boolean getShouldVis() { return this.shouldVis; }
	
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

			if(currentDim==dimSizes.length)
				return null;

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
