package com.nepalaccountant.backend.exception;

import com.nepalaccountant.backend.dto.ApiErrorResponse;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST API endpoints.
 * 
 * Centralizes exception handling across the application and returns consistent,
 * structured error responses to API clients.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handles validation errors from invalid request data.
	 * 
	 * Intercepts {@link MethodArgumentNotValidException} thrown when request body validation fails
	 * and returns a detailed error response with field-level validation messages.
	 * 
	 * @param exception the validation exception
	 * @return a {@link ResponseEntity} with 400 Bad Request and detailed error information
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
		List<String> details = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.toList();

		return ResponseEntity.badRequest().body(new ApiErrorResponse(
				"VALIDATION_ERROR",
				"Request validation failed",
				details,
				OffsetDateTime.now()
		));
	}

	/**
	 * Handles illegal state errors from service operations.
	 * 
	 * Intercepts {@link IllegalStateException} thrown when services encounter configuration
	 * or operational errors, and returns a 503 Service Unavailable response.
	 * 
	 * @param exception the illegal state exception
	 * @return a {@link ResponseEntity} with 503 Service Unavailable and error information
	 */
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException exception) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiErrorResponse(
				"SERVICE_UNAVAILABLE",
				exception.getMessage(),
				List.of(),
				OffsetDateTime.now()
		));
	}

	/**
	 * Handles authentication errors from Supabase operations.
	 * 
	 * Intercepts {@link SupabaseAuthException} thrown during authentication failures
	 * and returns an appropriate error response with the status provided by the exception.
	 * 
	 * @param exception the Supabase authentication exception
	 * @return a {@link ResponseEntity} with the appropriate HTTP status and error information
	 */
	@ExceptionHandler(SupabaseAuthException.class)
	public ResponseEntity<ApiErrorResponse> handleSupabaseAuth(SupabaseAuthException exception) {
		return ResponseEntity.status(exception.status()).body(new ApiErrorResponse(
				"SUPABASE_AUTH_ERROR",
				exception.getMessage(),
				List.of(),
				OffsetDateTime.now()
		));
	}

	/**
	 * Handles authorization errors when users lack proper credentials.
	 * 
	 * Intercepts {@link UnauthorizedException} thrown when access tokens are missing,
	 * invalid, or expired, and returns a 401 Unauthorized response.
	 * 
	 * @param exception the unauthorized exception
	 * @return a {@link ResponseEntity} with 401 Unauthorized and error information
	 */
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiErrorResponse> handleUnauthorized(UnauthorizedException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorResponse(
				"UNAUTHORIZED",
				exception.getMessage(),
				List.of(),
				OffsetDateTime.now()
		));
	}

	/**
	 * Handles resource not found errors when requested entities do not exist.
	 * 
	 * Intercepts {@link ResourceNotFoundException} thrown when requested resources
	 * (businesses, invoices, etc.) cannot be found, and returns a 404 Not Found response.
	 * 
	 * @param exception the resource not found exception
	 * @return a {@link ResponseEntity} with 404 Not Found and error information
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(
				"NOT_FOUND",
				exception.getMessage(),
				List.of(),
				OffsetDateTime.now()
		));
	}

}
