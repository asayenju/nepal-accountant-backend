package com.nepalaccountant.backend.service;

import com.nepalaccountant.backend.config.SupabaseProperties;
import com.nepalaccountant.backend.dto.AuthenticatedUser;
import com.nepalaccountant.backend.dto.SupabaseAuthErrorResponse;
import com.nepalaccountant.backend.dto.SupabaseUserProfileResponse;
import com.nepalaccountant.backend.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/**
 * Service for validating and managing user authentication sessions with Supabase.
 * 
 * Verifies access tokens and retrieves authenticated user information
 * for authorization in protected API endpoints.
 */
@Service
public class AuthSessionService {

	private final SupabaseProperties supabaseProperties;
	private final WebClient supabaseAnonWebClient;

	/**
	 * Constructs an AuthSessionService with Supabase client and properties.
	 *
	 * @param supabaseProperties contains Supabase configuration (URL, keys)
	 * @param supabaseAnonWebClient the WebClient configured for Supabase anonymous requests
	 */
	public AuthSessionService(
			SupabaseProperties supabaseProperties,
			@Qualifier("supabaseAnonWebClient") WebClient supabaseAnonWebClient
	) {
		this.supabaseProperties = supabaseProperties;
		this.supabaseAnonWebClient = supabaseAnonWebClient;
	}

	/**
	 * Validates an access token and retrieves the associated user information.
	 * 
	 * Verifies the provided access token with Supabase and returns the authenticated
	 * user's ID and email address. Used by authentication interceptors for validating
	 * incoming requests.
	 * 
	 * @param accessToken the JWT access token to validate
	 * @return an {@link AuthenticatedUser} containing the user ID and email
	 * @throws UnauthorizedException if the token is invalid, expired, or verification fails
	 * @throws IllegalStateException if Supabase configuration is missing or unreachable
	 */
	public AuthenticatedUser validateAccessToken(String accessToken) {
		validateAuthConfig();

		try {
			SupabaseUserProfileResponse response = supabaseAnonWebClient.get()
					.uri("/auth/v1/user")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.retrieve()
					.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(SupabaseAuthErrorResponse.class)
							.map(error -> new UnauthorizedException(resolveErrorMessage(error))))
					.bodyToMono(SupabaseUserProfileResponse.class)
					.block();

			if (response == null || response.id() == null) {
				throw new UnauthorizedException("Access token is invalid or expired.");
			}

			return new AuthenticatedUser(response.id(), response.email());
		} catch (WebClientRequestException exception) {
			throw new IllegalStateException("Failed to validate access token with Supabase. Check SUPABASE_URL and network connectivity.");
		}
	}

	/**
	 * Validates that Supabase authentication session operations are properly configured.
	 * 
	 * Checks that SUPABASE_URL and SUPABASE_ANON_KEY are configured with real values.
	 * 
	 * @throws IllegalStateException if required Supabase properties are not configured
	 */
	private void validateAuthConfig() {
		if (!supabaseProperties.hasConfiguredUrl()) {
			throw new IllegalStateException("SUPABASE_URL is not configured. Set it to your real Supabase project URL before using protected endpoints.");
		}
		if (!supabaseProperties.hasConfiguredAnonKey()) {
			throw new IllegalStateException("SUPABASE_ANON_KEY is not configured. Set it before using protected endpoints.");
		}
	}

	/**
	 * Extracts a human-readable error message from a Supabase authentication error response.
	 * 
	 * Attempts to extract the error message from multiple possible fields in the Supabase response,
	 * returning a fallback message if none are available.
	 * 
	 * @param error the Supabase authentication error response
	 * @return the resolved error message or fallback
	 */
	private String resolveErrorMessage(SupabaseAuthErrorResponse error) {
		if (error == null) {
			return "Access token is invalid or expired.";
		}
		if (hasText(error.message())) {
			return error.message();
		}
		if (hasText(error.msg())) {
			return error.msg();
		}
		if (hasText(error.errorDescription())) {
			return error.errorDescription();
		}
		return "Access token is invalid or expired.";
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
