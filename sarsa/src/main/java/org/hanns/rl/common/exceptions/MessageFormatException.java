package org.hanns.rl.common.exceptions;

/**
 * ROS message received has wrong format (e.g. dimensionality of data)  
 *  
 * @author Jaroslav Vitku
 *
 */
public class MessageFormatException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message Text explanation of the exception. 
	 */
	public MessageFormatException(String message) {
		super(message);
	}

	/**
	 * @param cause Another throwable that indicates a problem underlying this 
	 * 		exception.  
	 */
	public MessageFormatException(Throwable cause) {
		super(cause); 
	}

	/**
	 * @param message Text explanation of the exception. 
	 * @param cause Another throwable that indicates a problem underlying this 
	 * 		exception.  
	 */
	public MessageFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
