package org.hanns.rl.discrete.observer.qMatrix.stats;

import org.hanns.rl.discrete.observer.qMatrix.QMatrixObserver;

/**
 * Observes QMatrix values and writes them into file.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface QMatrixFileWriter extends QMatrixObserver{

	public String getFileName();	
	
	/**
	 * Set rounding to the file (e.g. avoid no. formatting as: 2.25E-12)
	 * @param round e.g. 10, 100, 100000
	 */
	public void setRounding(int round);
	
	public int getRounding();
}
