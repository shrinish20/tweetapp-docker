package com.tweetapp.exception;

public class EntityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 *  Constructor
	 */
	public EntityNotFoundException() {
	}

	/** Constructor
	 * @param message
	 */
	public EntityNotFoundException(String message) {
		super(message);
	}
	
}
