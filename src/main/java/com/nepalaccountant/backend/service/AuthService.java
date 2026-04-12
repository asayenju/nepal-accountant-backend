package com.nepalaccountant.backend.service;

import com.nepalaccountant.backend.dto.AuthLoginRequest;
import com.nepalaccountant.backend.dto.AuthSignupRequest;
import com.nepalaccountant.backend.dto.LoginResponse;
import com.nepalaccountant.backend.dto.SignupResponse;
import com.nepalaccountant.backend.dto.SupabaseAuthErrorResponse;
import com.nepalaccountant.backend.dto.SupabaseAuthResponse;
import com.nepalaccountant.backend.exception.SupabaseAuthException;
import java.util.HashMap;
import java.util.Map;
import com.nepalaccountant.backend.config.SupabaseProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service for handling authentication operations with Supabase.
 * 
 * Manages user signup and login processes by communicating with Supabase authentication API.
 * Handles user profile data, token generation, and error responses from Supabase.
 */
@Service
public class AuthService {

	private final WebClient supabaseAnonWebClient;
	private final SupabaseProperties supabaseProperties;

	/**
	 * Constructs an AuthService with Supabase client and properties.
	 *
	 * @param supabaseProperties contains Supabase configuration (URL, keys)
	 * @param supabaseAnonWebClient the WebClient configured for Supabase anonymous requests
	 */
	public AuthService(
			SupabaseProperties supabaseProperties,
			@Qualifier("supabaseAnonWebClient") WebClient supabaseAnonWebClient
	) {
		this.supabaseProperties = supabaseProperties;
		this.supabaseAnonWebClient = supabaseAnonWebClient;
	}

	/**
	 * Registers a new user with Supabase authentication.
	 * 
	 * Creates a new user account with email, password, and optional profile metadata
	 * (first name, last name, phone number). If email confirmation is configured in Supabase,
	 * the user will need to verify their email before accessing the account.
	 * 
	 * @param request the signup request containing email, password, and optional user profile details
	 * @return a {@link SignupResponse} with user ID, email, confirmation status, and message
	 * @throws IllegalStateException if Supabase URL or keys are not configured, or signup fails
	 * @throws SupabaseAuthException if Supabase returns an authentication error (e.g., email already exists)
	 */
	public SignupResponse signup(AuthSignupRequest request) {
		validateAuthConfig();

		Map<String, Object> metadata = new HashMap<>();
		metadata.put("first_name", request.firstName());
		metadata.put("last_name", normalizeBlank(request.lastName()));
		metadata.put("phone", normalizeBlank(request.phone()));

		Map<String, Object> payload = Map.of(
				"email", request.email(),
				"password", request.password(),
				"data", metadata
		);

		SupabaseAuthResponse response;
		try {
			response = supabaseAnonWebClient.post()
					.uri("/auth/v1/signup")
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(payload)
					.retrieve()
					.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(SupabaseAuthErrorResponse.class)
							.map(error -> new SupabaseAuthException(
									resolveStatus(clientResponse.statusCode()),
									resolveErrorMessage(error, "Supabase signup failed")
							)))
					.bodyToMono(SupabaseAuthResponse.class)
					.block();
		} catch (WebClientRequestException exception) {
			throw new IllegalStateException(buildConnectivityMessage(exception));
		}

		if (response == null || response.user() == null) {
			throw new IllegalStateException("Supabase did not return a user during signup");
		}

		boolean requiresEmailConfirmation = response.resolvedAccessToken() == null;

		return new SignupResponse(
				response.user().id(),
				response.user().email(),
				requiresEmailConfirmation,
				requiresEmailConfirmation
						? "Signup successful. Please verify your email before logging in."
						: "Signup successful. Please log in to receive an access token."
		);
	}

	/**
	 * Authenticates a user with email and password credentials.
	 * 
	 * Validates the provided email and password against Supabase authentication.
	 * Upon successful authentication, returns access tokens for subsequent API requests.
	 * 
	 * @param request the login request containing email and password
	 * @return a {@link LoginResponse} with access token, refresh token, user info, and expiration
	 * @throws IllegalStateException if Supabase URL or keys are not configured, or login fails
	 * @throws SupabaseAuthException if Supabase returns an authentication error (e.g., invalid credentials)
	 */
	public LoginResponse login(AuthLoginRequest request) {
		validateAuthConfig();

		Map<String, Object> payload = Map.of(
				"email", request.email(),
				"password", request.password()
		);

		SupabaseAuthResponse response;
		try {
			response = supabaseAnonWebClient.post()
					.uri("/auth/v1/token?grant_type=password")
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(payload)
					.retrieve()
					.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(SupabaseAuthErrorResponse.class)
							.map(error -> new SupabaseAuthException(
									resolveStatus(clientResponse.statusCode()),
									resolveErrorMessage(error, "Invalid email or password")
							)))
					.bodyToMono(SupabaseAuthResponse.class)
					.block();
		} catch (WebClientRequestException exception) {
			throw new IllegalStateException(buildConnectivityMessage(exception));
		}

		if (response == null || response.user() == null || response.resolvedAccessToken() == null) {
			throw new IllegalStateException("Supabase did not return a valid login session");
		}

		return new LoginResponse(
				response.user().id(),
				response.user().email(),
				response.resolvedAccessToken(),
				response.resolvedRefreshToken(),
				response.resolvedTokenType(),
				response.resolvedExpiresIn(),
				"Login successful."
		);
	}

	/**
	 * Validates that Supabase authentication is properly configured.
	 * 
	 * Checks that SUPABASE_URL and SUPABASE_ANON_KEY are configured with real values.
	 * 
	 * @throws IllegalStateException if required Supabase properties are not configured
	 */
	private void validateAuthConfig() {
		if (!supabaseProperties.hasConfiguredUrl()) {
			throw new IllegalStateException("SUPABASE_URL is not configured. Set it to your real Supabase project URL before using auth endpoints.");
		}
		if (!supabaseProperties.hasConfiguredAnonKey()) {
			throw new IllegalStateException("SUPABASE_ANON_KEY is not configured. Set it before using auth endpoints.");
		}
		if (!supabaseProperties.hasConfiguredServiceRoleKey()) {
			return;
		}
	}

	/**
	 * Constructs an appropriate error message for connectivity issues.
	 * 
	 * Returns a detailed message indicating the likely cause of Supabase communication failure.
	 * 
	 * @param exception the WebClientRequestException that occurred
	 * @return a descriptive error message for connectivity troubleshooting
	 */
	private String buildConnectivityMessage(WebClientRequestException exception) {
		if (!supabaseProperties.hasConfiguredUrl()) {
			return "SUPABASE_URL is not configured. Set it to your real Supabase project URL before using auth endpoints.";
		}
		return "Failed to reach Supabase. Check SUPABASE_URL, network connectivity, and whether your Supabase project is reachable.";
	}

	/**
	 * Resolves an HTTP status code to an appropriate HttpStatus for authentication errors.
	 * 
	 * Maps Supabase error status codes to standardized HTTP status codes for consistency.
	 * 
	 * @param statusCode the HTTP status code returned by Supabase
	 * @return an appropriate {@link HttpStatus} for the error
	 */
	private HttpStatus resolveStatus(HttpStatusCode statusCode) {
		HttpStatus status = HttpStatus.resolve(statusCode.value());
		if (status == null) return HttpStatus.BAD_GATEWAY;
		if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) return HttpStatus.UNAUTHORIZED;
		if (status == HttpStatus.CONFLICT) return HttpStatus.CONFLICT;
		if (status == HttpStatus.BAD_REQUEST) return HttpStatus.BAD_REQUEST;
		return HttpStatus.BAD_GATEWAY;
	}

	/**
	 * Extracts a human-readable error message from a Supabase authentication error response.
	 * 
	 * Attempts to extract the error message from multiple possible fields in the Supabase response,
	 * returning a fallback message if none are available.
	 * 
	 * @param error the Supabase authentication error response
	 * @param fallbackMessage the message to return if no error message is found
	 * @return the resolved error message or fallback
	 */
	private String resolveErrorMessage(SupabaseAuthErrorResponse error, String fallbackMessage) {
		if (error == null) return fallbackMessage;
		if (hasText(error.message())) return error.message();
		if (hasText(error.msg())) return error.msg();
		if (hasText(error.errorDescription())) return error.errorDescription();
		return fallbackMessage;
	}

	/**
	 * Normalizes a string value by trimming whitespace and converting empty strings to null.
	 * 
	 * @param value the string to normalize
	 * @return the trimmed value or null if the original value is empty or whitespace-only
	 */
	private String normalizeBlank(String value) {
		return hasText(value) ? value.trim() : null;
	}

	/**
	 * Checks if a string has meaningful text content.
	 * 
	 * @param value the string to check
	 * @return true if the value is not null and contains non-whitespace characters, false otherwise
	 */
	private boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
	}
}
