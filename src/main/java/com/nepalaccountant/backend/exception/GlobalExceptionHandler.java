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

@RestControllerAdvice
public class GlobalExceptionHandler {

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

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException exception) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiErrorResponse(
				"SERVICE_UNAVAILABLE",
				exception.getMessage(),
				List.of(),
				OffsetDateTime.now()
		));
	}

	@ExceptionHandler(SupabaseAuthException.class)
	public ResponseEntity<ApiErrorResponse> handleSupabaseAuth(SupabaseAuthException exception) {
		return ResponseEntity.status(exception.status()).body(new ApiErrorResponse(
				"SUPABASE_AUTH_ERROR",
				exception.getMessage(),
				List.of(),
				OffsetDateTime.now()
		));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiErrorResponse> handleUnauthorized(UnauthorizedException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorResponse(
				"UNAUTHORIZED",
				exception.getMessage(),
				List.of(),
				OffsetDateTime.now()
		));
	}

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
