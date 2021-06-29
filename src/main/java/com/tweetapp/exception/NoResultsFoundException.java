package com.tweetapp.exception;

public class NoResultsFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 */
	public NoResultsFoundException() {
		super();
	}

	/**
	 * Constructor
	 * @param message
	 */
	public NoResultsFoundException(String message) {
		super(message);
	}
	
	
	
}
