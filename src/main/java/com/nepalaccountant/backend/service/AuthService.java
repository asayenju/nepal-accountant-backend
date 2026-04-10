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

@Service
public class AuthService {

	private final WebClient supabaseAnonWebClient;
	private final SupabaseProperties supabaseProperties;

	public AuthService(
			SupabaseProperties supabaseProperties,
			@Qualifier("supabaseAnonWebClient") WebClient supabaseAnonWebClient
	) {
		this.supabaseProperties = supabaseProperties;
		this.supabaseAnonWebClient = supabaseAnonWebClient;
	}

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

	private String buildConnectivityMessage(WebClientRequestException exception) {
		if (!supabaseProperties.hasConfiguredUrl()) {
			return "SUPABASE_URL is not configured. Set it to your real Supabase project URL before using auth endpoints.";
		}
		return "Failed to reach Supabase. Check SUPABASE_URL, network connectivity, and whether your Supabase project is reachable.";
	}

	private HttpStatus resolveStatus(HttpStatusCode statusCode) {
		HttpStatus status = HttpStatus.resolve(statusCode.value());
		if (status == null) return HttpStatus.BAD_GATEWAY;
		if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) return HttpStatus.UNAUTHORIZED;
		if (status == HttpStatus.CONFLICT) return HttpStatus.CONFLICT;
		if (status == HttpStatus.BAD_REQUEST) return HttpStatus.BAD_REQUEST;
		return HttpStatus.BAD_GATEWAY;
	}

	private String resolveErrorMessage(SupabaseAuthErrorResponse error, String fallbackMessage) {
		if (error == null) return fallbackMessage;
		if (hasText(error.message())) return error.message();
		if (hasText(error.msg())) return error.msg();
		if (hasText(error.errorDescription())) return error.errorDescription();
		return fallbackMessage;
	}

	private String normalizeBlank(String value) {
		return hasText(value) ? value.trim() : null;
	}

	private boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
	}
}
