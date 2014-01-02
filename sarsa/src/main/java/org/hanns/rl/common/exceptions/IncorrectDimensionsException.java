package org.hanns.rl.common.exceptions;

/**
 * Thrown during the attempt to set or use some data structure with incorrect
 * dimensionality. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class IncorrectDimensionsException extends Exception{
	
	private static final long serialVersionUID = 1L;

	/**
	 * @param message Text explanation of the exception. 
	 */
	public IncorrectDimensionsException(String message) {
		super(message);
	}

	/**
	 * @param cause Another throwable that indicates a problem underlying this 
	 * 		exception.  
	 */
	public IncorrectDimensionsException(Throwable cause) {
		super(cause); 
	}

	/**
	 * @param message Text explanation of the exception. 
	 * @param cause Another throwable that indicates a problem underlying this 
	 * 		exception.  
	 */
	public IncorrectDimensionsException(String message, Throwable cause) {
		super(message, cause);
	}
}
