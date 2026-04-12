package com.nepalaccountant.backend.exception;

/**
 * Exception thrown when a user is not properly authenticated or authorized to access a resource.
 * 
 * Typically indicates missing or invalid authentication credentials.
 */
public class UnauthorizedException extends RuntimeException {

	/**
	 * Constructs an UnauthorizedException with a descriptive message.
	 *
	 * @param message the detail message explaining the authorization failure
	 */
	public UnauthorizedException(String message) {
		super(message);
	}

}
