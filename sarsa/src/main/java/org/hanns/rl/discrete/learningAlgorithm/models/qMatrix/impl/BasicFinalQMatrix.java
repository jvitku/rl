package org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.impl;

import java.util.Random;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.dataStructure.MultiDimensionMatrix;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.dataStructure.impl.StaticMultiDimension;

public class BasicFinalQMatrix implements FinalQMatrix<Double>{

	private int[] dimensionSizes;

	private MultiDimensionMatrix<Double> d;

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
		
		int numofcells = numActions;
		for(int i=0; i<numVariables; i++){
			numofcells = numofcells * this.varRanges[i];
		}
		
		// TODO add structure with dynamic memory allocation (allocate only if necessary)
		if(numofcells > 5000000){
			System.err.println("BasicFInalQMatrix: WARNING: number of cells you are"
					+ "trying to instantly allocate is "+numofcells+
					" this might take a while if is allocated at once..");
		}
		
		// initialize the main data structure with the default value
		d = new StaticMultiDimension<Double>(dimensionSizes, 0, this.defValue);
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
			if(d instanceof StaticMultiDimension)
				((StaticMultiDimension<Double>)d).setAllVals(this.defValue);
		}
	}

	@Override
	public void set(int[] coordinates, Double value) {
		d.setValue(coordinates, value);
	}

	@Override
	public Double get(int[] coordinates) {
		if(coordinates.length!=this.numDimensions){
			System.err.println("BasicFinalWMatrix: wrong number of dimensions!");
			return null;
		}
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
	


}

