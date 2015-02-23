package org.hanns.rl.discrete.observer.qMatrix.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;

import ctu.nengoros.network.common.Resettable;
import ctu.nengoros.util.SL;

public abstract class FinalMaxActionUtil<E> implements QMatrixFileWriter {

	public final String name = "FinalMaxActionUtil";
	public final String me = "["+name+"] ";

	public static final boolean DEF_SHOULDWRITE = true;

	// visualize each 100 steps by default
	public static final int DEF_VISPERIOD = 100;

	private final int[] dimSizes;
	protected final int noActions;
	private final FinalQMatrix<E> q;

	private int visPeriod = DEF_VISPERIOD;			
	private boolean shouldWrite = DEF_SHOULDWRITE;

	public static final String NO_ACTION = " ";
	public static final String NO_VALUE = ".";

	private final String filename;

	private int step;

	public static final int DEF_PRECISION = 15;	// how many digits after dot?
	private int precision;

	// append new data to the end of the file or overwrite?
	public static final boolean DEF_APPEND = false;	
	private boolean append = DEF_APPEND;

	public static final boolean DEF_FORMATTING = false;
	private boolean formatting = DEF_FORMATTING;

	private DecimalFormat format;
	
	SL logger;
	
	public boolean logToConsole = false;

	public FinalMaxActionUtil(int[] dimSizes, int noActions, FinalQMatrix<E> q, String filename){
		this.dimSizes = dimSizes.clone();
		this.noActions = noActions;
		this.q = q;

		this.filename = filename;
		logger = new SL(filename, false);

		this.precision = DEF_PRECISION;
		this.generateFormat();

		this.softReset(false);
	}
	
	private void generateFormat(){
		String pat = "#.";
		for(int i=0; i<this.precision; i++)
			pat +="#";
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		otherSymbols.setDecimalSeparator('.');
		format = new DecimalFormat(pat,otherSymbols);
	}

	@Override
	public void setAppendData(boolean append) { this.append = append; }

	@Override
	public void setUseFormatting(boolean useFormatting) { this.formatting = useFormatting; }

	@Override
	public void observe(int prevAction, float reward, int[] currentState, int futureAction) {
		if( this.visPeriod < 0 || !this.shouldWrite)
			return;

		if( step++ % visPeriod != 0 )
			return;

		this.write();

		//System.out.println(me+"step no: "+step+"\n"+this.visualize());

	}

	public static final String SEPARATOR = "\t";
	public static final String LINE = "\t------------------------";

	private void write(){


		String out = "";

		DimCounter dc = new DimCounter(dimSizes);
		dc.softReset(false);
		int[] coords = dc.getCurrentCoords();	// get all zeros

		// while there is still something to iterate
		while(true){

			if(this.formatting)
				out = out + LINE+" these dimensions of Q matrix are displayed: "
						+this.writeDims(coords);

			// the y dimension, from the biggest index towards 0 
			for(int j=dimSizes[1]-1; j>=0; j--){
				coords[1] = j;

				if(this.formatting)				// draws the y axis
					out = out + "\n"+j+"\t| ";
				
				else if(j != dimSizes[1]-1)		// omit the first enter
					out = out + "\n";

				// the x dimension, from the left to right, from 0 towards end
				for(int i=0; i<dimSizes[0]; i++){
					coords[0] = i;
					String sep;
					if(i==0)
						sep="";
					else
						sep=SEPARATOR;

					int ind = this.getMaxActionInd(coords);
					double val = (Double)q.getActionValsInState(coords)[ind];

					//String no = String.format(Locale.ENGLISH,"%f", val);
					/*
					DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
					otherSymbols.setDecimalSeparator('.');
					DecimalFormat format = new DecimalFormat(pattern,otherSymbols);
					*/
					
					//String no = new DecimalFormat(pattern,otherSymbols).format(val);
					out = out + sep + format.format(val);
				}
			}

			// append X axis
			if(this.formatting){
				out = out+"\nY \t_____";
				for(int i=1; i<dimSizes[0]; i++){
					out = out +SEPARATOR+ "______";
				}
				out = out +"\n   X \t 0";
				for(int i=1; i<dimSizes[0]; i++){
					out = out +SEPARATOR+ i;
				}
			}
			coords = dc.next();
			if(coords==null)
				break;
		}

		if(this.logToConsole)
			System.out.println(out+"\n");

		//logger.printToFile(false);

		if(!this.append)
			logger.cleanupFile();
		logger.pl(out+"\n");


		//return out+"\n";
	}

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
	public void softReset(boolean randomize) {
		this.step = 0;
	}

	@Override
	public void hardReset(boolean randomize) {
		this.softReset(randomize);
	}

	@Override
	public void setShouldVis(boolean visualize) { this.shouldWrite = visualize;	}

	@Override
	public boolean getShouldVis() { return this.shouldWrite; }

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

	@Override
	public int getVisDetails() { return 0; }

	@Override
	public void setVisDetails(int arg0) {}

	@Override
	public String getName() { return name; }

	@Override
	public String getFileName() { return this.filename; }

	
	@Override
	public void setPrecision(int precision) { 
		if(precision<0)
			precision =1;
		this.precision = precision;
		this.generateFormat();
		
	}

	@Override
	public int getPrecision() { return this.precision; }

}

