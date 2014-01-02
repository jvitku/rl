package org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.dataStructure;

public interface MultiDimensionMatrix<E> {
	
	/**
	 * If not, we should print out warning about too many cells
	 * @return true if the structure is allocated dynamically (online) 
	 */
	public boolean isDynamicallyAllocated();
	
	/**
	 * Set value to given coordination 
	 * @param coords array of coordinations
	 * @param value value to be set
	 */
	public void setValue(int[] coords, E value);
	
	/**
	 * Read value from given coordinations
	 * @param coords array of integer valued coordinations inn the matrix
	 * @return value on the coordinations or defValue if not allocated
	 */
	public E readValue(int[] coords);




}
