package com.nepalaccountant.backend.exception;

/**
 * Exception thrown when a requested resource is not found in the system.
 * 
 * Typically indicates that a resource with the requested ID or identifier does not exist.
 */
public class ResourceNotFoundException extends RuntimeException {

	/**
	 * Constructs a ResourceNotFoundException with a descriptive message.
	 *
	 * @param message the detail message explaining what resource was not found
	 */
	public ResourceNotFoundException(String message) {
		super(message);
	}

}
