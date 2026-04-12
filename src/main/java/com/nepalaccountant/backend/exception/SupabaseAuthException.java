package com.nepalaccountant.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when Supabase authentication operations fail.
 * 
 * Wraps authentication-related errors from Supabase with appropriate HTTP status codes.
 */
public class SupabaseAuthException extends RuntimeException {

	private final HttpStatus status;

	/**
	 * Constructs a SupabaseAuthException with status and message.
	 *
	 * @param status the HTTP status code to return to the client
	 * @param message the detail message explaining the authentication error
	 */
	public SupabaseAuthException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	/**
	 * Gets the HTTP status code associated with this authentication error.
	 *
	 * @return the {@link HttpStatus} to return to the client
	 */
	public HttpStatus status() {
		return status;
	}

}
