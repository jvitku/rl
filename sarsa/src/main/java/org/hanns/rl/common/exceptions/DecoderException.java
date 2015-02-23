package org.hanns.rl.common.exceptions;

/**
 * Thrown if the message could not be decoded. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class DecoderException extends Exception{
	
	private static final long serialVersionUID = 1L;

	/**
	 * @param message Text explanation of the exception. 
	 */
	public DecoderException(String message) {
		super(message);
	}

	/**
	 * @param cause Another throwable that indicates a problem underlying this 
	 * 		exception.  
	 */
	public DecoderException(Throwable cause) {
		super(cause); 
	}

	/**
	 * @param message Text explanation of the exception. 
	 * @param cause Another throwable that indicates a problem underlying this 
	 * 		exception.  
	 */
	public DecoderException(String message, Throwable cause) {
		super(message, cause);
	}

}
