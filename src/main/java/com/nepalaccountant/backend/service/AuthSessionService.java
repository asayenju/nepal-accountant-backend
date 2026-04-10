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

@Service
public class AuthSessionService {

	private final SupabaseProperties supabaseProperties;
	private final WebClient supabaseAnonWebClient;

	public AuthSessionService(
			SupabaseProperties supabaseProperties,
			@Qualifier("supabaseAnonWebClient") WebClient supabaseAnonWebClient
	) {
		this.supabaseProperties = supabaseProperties;
		this.supabaseAnonWebClient = supabaseAnonWebClient;
	}

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

	private void validateAuthConfig() {
		if (!supabaseProperties.hasConfiguredUrl()) {
			throw new IllegalStateException("SUPABASE_URL is not configured. Set it to your real Supabase project URL before using protected endpoints.");
		}
		if (!supabaseProperties.hasConfiguredAnonKey()) {
			throw new IllegalStateException("SUPABASE_ANON_KEY is not configured. Set it before using protected endpoints.");
		}
	}

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

	private boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
	}

}
