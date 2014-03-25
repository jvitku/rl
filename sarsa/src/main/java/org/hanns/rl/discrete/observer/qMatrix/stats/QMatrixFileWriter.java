package org.hanns.rl.discrete.observer.qMatrix.stats;

import org.hanns.rl.discrete.observer.qMatrix.QMatrixObserver;

/**
 * Observes QMatrix values and writes them into file.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface QMatrixFileWriter extends QMatrixObserver{

	/**
	 * If append is set to false, all data in the text file are deleted each log. 
	 * This means that only one matrix (the latest one) is stored.
	 * 
	 * If append is true, data are appended sequentially into the file.
	 * @param append
	 */
	public void setAppendData(boolean append);
	
	/**
	 * If useFormatting is false, bare matrixes of numbers (tab-separated) are stored.
	 *	If the append is true, each matrix is separated by white line.
	 *  
	 * @param useFormatting if false, no formatting is appended
	 */
	public void setUseFormatting(boolean useFormatting);
	
	public String getFileName();	
	
	/**
	 * Set rounding to the file (e.g. avoid no. formatting as: 2.25E-12)
	 * @param round e.g. 10, 100, 100000
	 *
	public void setRounding(int round);
	
	public int getRounding();
	*/
}
