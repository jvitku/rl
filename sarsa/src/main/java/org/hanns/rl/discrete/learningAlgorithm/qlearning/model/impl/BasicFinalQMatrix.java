package org.hanns.rl.discrete.learningAlgorithm.qlearning.model.impl;

import java.util.Random;

import org.hanns.rl.discrete.learningAlgorithm.qlearning.model.FinalQMatrix;

public class BasicFinalQMatrix implements FinalQMatrix<Double>{

	private int[] dimensionSizes;

	private Dimension<Double> d;

	private Double defValue = 0.0;
	private Double minRand = 0.0, range = 1.0;
	
	private final int[] varRanges;
	private final int numActions;
	private final int numVariables;
	private final int numDimensions;
	
	final Random r = new Random();
	
	public BasicFinalQMatrix(int[] stateVariableRanges, int numActions){
		
		this.varRanges = stateVariableRanges.clone();
		this.numActions = numActions;
		this.numVariables =this.varRanges.length;
		this.numDimensions = this.varRanges.length+1;
		
		this.dimensionSizes = new int[this.numDimensions];
		
		// dimensions are indexed by state variables (except the last one) 
		for(int i=0; i<this.numVariables; i++){
			this.dimensionSizes[i] = this.varRanges[i];
		}
		// the last dimension represents actions
		this.dimensionSizes[this.dimensionSizes.length-1] = numActions;
		
		// initialize the main data structure with the default value
		d = new Dimension<Double>(dimensionSizes, 0, this.defValue);
	}

	@Override
	public double[] getRandomizeRange(){ return new double[]{minRand, range}; }
	
	@Override
	public Double getDefaultValue(){ return this.defValue; }
	
	@Override
	public int[] getDimensionSizes() { return this.dimensionSizes; }

	@Override
	public int getNumActions() { return this.numActions; }

	@Override
	public int getNumStateVariables() { return this.numVariables; }
	
	@Override
	public void softReset(boolean randomize) {}

	@Override
	public void hardReset(boolean randomize) {
		if(randomize){
			int[] startInds = new int[this.dimensionSizes.length];
			for(int i=0; i<startInds.length; i++)
				startInds[i] = 0;
			// recursively randomize every value in the matrix
			this.randomizeDimension(startInds, 0);
			
		}else{
			d.setAllVals(this.defValue);
		}
	}

	@Override
	public void set(int[] coordinates, Double value) {
		d.setValue(coordinates, value);
	}

	@Override
	public Double get(int[] coordinates) {
		return d.readValue(coordinates);
	}

	/**
	 * Note: this could be implemented in a more elegant way:
	 * method in {@link #d} for recursive call to the last dimension and then 
	 * collect values and return it.   
	 */
	@Override
	public Double[] getActionValsInState(int[] states) {
		
		if(states.length!=this.numDimensions-1){
			System.err.println("BasicFinalQMatrix: iincorrect length" +
					"of index array!");
			return null;
		}
		
		Double[] vals = new Double[this.numActions];
		int[] coords = new int[this.numDimensions];
		
		for(int i=0; i<states.length; i++)
			coords[i] = states[i];
		
		for(int i=0; i<this.numActions; i++){
			// set the coordinate for action to read and read it
			coords[this.numDimensions-1] = i;	
			vals[i] = d.readValue(coords);
		}
		return vals;
	}

	@Override
	public void setDefaultValue(Double defValue) {
		this.defValue = defValue;
	}

	@Override
	public void setRandomizationParameters(Double minValue, Double maxValue) {
		if(minValue>=maxValue){
			System.err.println("BasicFinalQMatrix: incorrect definition of min,max values");
			return;
		}
		this.minRand = minValue;
		this.range = maxValue-minValue;
	}
	

	/**
	 * Recursively randomize every value in the matrix 
	 * @param indexes should be called externally with array of indexes with all zeros
	 * @param depth current depth of recursion, should be called externaly with value of 0
	 */
	private void randomizeDimension(int[] indexes, int depth){
		// recursion done
		if(depth==this.dimensionSizes.length){
			
			// generate double in the range
			Double v = this.minRand+(r.nextDouble()*this.range);
			d.setValue(indexes, v);
		}
		int[] tmp;
		
		// randomize all participants in this dimension
		for(int i=0; i<this.dimensionSizes[depth]; i++){
			tmp = indexes;
			tmp[depth] = i;
			this.randomizeDimension(tmp, depth+1);
		}
	}
	
	/**
	 * Multidimensional class with definable final number of dimensions
	 * and dimension sizes. The dimension sizes are defined as an array of
	 * integer values. The parameter index is used internally, so 
	 * the constructor should be called with index=0. 
	 *  
	 * @author Jaroslav Vitku
	 *
	 * @param <E> initial value that is stored on all places of the matrix 
	 */
	private class Dimension<E>{

		private E val;
		private Dimension<E>[] childs;

		/**
		 * Call this to initialize the class
		 * @param sizes integer array of dimension sizes
		 * @param index used for recursion, call with value of 0!
		 * @param initVal initial value that is stored in entire matrix
		 */
		@SuppressWarnings("unchecked")
		public Dimension(int[] sizes, int index, E initVal){

			System.out.println("--- hi level "+index);
			// if not the last dimension, make array of childs and recurse
			if(index<sizes.length){

				int numChilds = sizes[index];
				System.out.println("------creating this no of childs: "+numChilds);

				childs = new Dimension[numChilds];
				for(int i=0; i<numChilds; i++){
					childs[i] = new Dimension<E>(sizes,index+1,initVal);
				}
			}else{
				System.out.println("STOP, setting my value to!"+initVal);
				this.val = initVal;
			}
		}

		/**
		 * Set given value on given coordinates 
		 * @param coords coordinates in the matrix
		 * @param value value to be set
		 */
		public void setValue(int[] coords, E value){
			this.setVal(coords, 0, value);
		}

		/**
		 * Read the value from the matrix from the given coordinates
		 * @param coords array of integer values - coordinates from which to read
		 * @return value stored on given coordinates
		 */
		public E readValue(int[] coords){
			return this.readValue(coords, 0);
		}
		
		/**
		 * Replace all values in the matrix with a given value
		 * @param value
		 */
		public void setAllVals(E value){
			// recursion done?
			if(childs==null){
				this.val = value;
				return;
			}
			// set values of all childs
			for(int i=0; i<childs.length; i++){
				this.setAllVals(value);
			}
		}

		/**
		 * Recursive call which sets given value on given coordinates
		 * @param coords indexes in the matrix
		 * @param depth current depth in the recursion (call with value of 0)
		 * @param value value to be set in the matrix on given coordinates
		 */
		private void setVal(int[] coords, int depth, E value){
			// traversing recursively across the coordinates
			if(depth<coords.length){
				System.out.println("rolling deepere "+depth);

				this.checkDims(coords, depth);
				childs[coords[depth]].setVal(coords, depth+1, value);

				// we are in the place (all coordinates applied)
			}else{
				System.out.println("SETTING this value "+value.toString());
				this.val = value;
			}
		}

		/**
		 * Used in recursion
		 * @param coords coordinates to be read
		 * @param depth current depth in the recursion
		 * @return value that is read
		 */
		private E readValue(int[] coords, int depth){
			if(depth<coords.length){
				System.out.println("rolling deepere "+depth);
				this.checkDims(coords, depth);
				return (E) childs[coords[depth]].readValue(coords, depth+1);
			}else{
				System.out.println("READING this value "+val.toString());
				return val;
			}
		}

		/**
		 * Check whether given coordinates are valid
		 * @param coords
		 * @param depth
		 */
		private boolean checkDims(int [] coords, int depth){
			if(coords[depth]<0){
				System.err.println("Dimension: negative index ");
				return false;
			}
			if(coords[depth]>=this.childs.length){
				System.err.println("Dimension: index out of range, this one: "+coords[depth]);
				return false;
			}
			return true;
		}
	}

}

