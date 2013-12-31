package org.hanns.rl.common.exceptions;

/**
 * The exception can be thrown during the attempt of changing the final  
 * parameter of an algorithm during the simulation. Similarly to final variables, 
 * final parameters should be set in the class constructors.  
 *  
 * @author Jaroslav Vitku
 *
 */
public class FinalParamException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message Text explanation of the exception. 
	 */
	public FinalParamException(String message) {
		super(message);
	}

	/**
	 * @param cause Another throwable that indicates a problem underlying this 
	 * 		exception.  
	 */
	public FinalParamException(Throwable cause) {
		super(cause); 
	}

	/**
	 * @param message Text explanation of the exception. 
	 * @param cause Another throwable that indicates a problem underlying this 
	 * 		exception.  
	 */
	public FinalParamException(String message, Throwable cause) {
		super(message, cause);
	}
}
