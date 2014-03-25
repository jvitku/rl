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
}
