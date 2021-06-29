package com.tweetapp.exception;

public class JwtTokenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public JwtTokenException() {
		super();
	}

	/**
	 * Constructor
	 * @param message
	 */
	public JwtTokenException(String message) {
		super(message);
	}
	
}
