package org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.dataStructure.impl;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.dataStructure.MultiDimensionMatrix;


	/**
	 * <p>Multidimensional class with definable final number of dimensions
	 * and dimension sizes. The dimension sizes are defined as an array of
	 * integer values. The parameter index is used internally, so 
	 * the constructor should be called with index=0.</p> 
	 * 
	 * <p>Note that memory for entire structure is allocated in the constructor. 
	 * The size of state space (faster for Q(s,a) space) grows exponentially, 
	 * so even for small number of variables and actions, the constructor can
	 * run really long.</p> 
	 * 
	 * TODO: add dynamically allocated structure
	 *  
	 * @author Jaroslav Vitku
	 *
	 * @param <E> initial value that is stored on all places of the matrix 
	 */
	public class PreAllocatedMultiDimension<E> implements MultiDimensionMatrix<E>{

		private E val;
		private PreAllocatedMultiDimension<E>[] childs;
		
		private final boolean dynamicallyAllocated = false;	 

		/**
		 * Call this to initialize the class
		 * @param sizes integer array of dimension sizes
		 * @param index used for recursion, call with value of 0!
		 * @param initVal initial value that is stored in entire matrix
		 */
		@SuppressWarnings("unchecked")
		public PreAllocatedMultiDimension(int[] sizes, int index, E initVal){

			//System.out.println("--- hi level "+index);
			// if not the last dimension, make array of childs and recurse
			if(index<sizes.length){

				int numChilds = sizes[index];
				//System.out.println("------creating this no of childs: "+numChilds);

				childs = new PreAllocatedMultiDimension[numChilds];
				for(int i=0; i<numChilds; i++){
					childs[i] = new PreAllocatedMultiDimension<E>(sizes,index+1,initVal);
				}
			}else{
				//System.out.println("STOP, setting my value to!"+initVal);
				this.val = initVal;
			}
		}

		/**
		 * Set given value on given coordinates 
		 * @param coords coordinates in the matrix
		 * @param value value to be set
		 */
		@Override
		public void setValue(int[] coords, E value){
			this.setVal(coords, 0, value);
		}

		/**
		 * Read the value from the matrix from the given coordinates
		 * @param coords array of integer values - coordinates from which to read
		 * @return value stored on given coordinates
		 */
		@Override
		public E readValue(int[] coords){
			return this.readValue(coords, 0);
		}
		
		/**
		 * Replace all values in the matrix with a given value
		 * @param value
		 */
		public void setAllVals(E value){
			// recursion done?
			if(childs == null){
				this.val = value;
				return;
			}
			// set values of all childs
			for(int i=0; i<childs.length; i++){
				childs[i].setAllVals(value);
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
				//System.out.println("rolling deepere "+depth);

				if(!this.checkDims(coords, depth))
					return;
				
				childs[coords[depth]].setVal(coords, depth+1, value);

				// we are in the place (all coordinates applied)
			}else{
				//System.out.println("SETTING this value "+value.toString());
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
				//System.out.println("rolling deepere "+depth);
				if(!this.checkDims(coords, depth))
					return null;
				return (E) childs[coords[depth]].readValue(coords, depth+1);
			}else{
				//System.out.println("READING this value "+val.toString());
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

		@Override
		public boolean isDynamicallyAllocated() { return this.dynamicallyAllocated; }
}
