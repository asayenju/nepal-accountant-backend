package com.nepalaccountant.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record SupabaseAuthResponse(
		SupabaseUser user,
		SupabaseSession session,
		@JsonProperty("access_token") String accessToken,
		@JsonProperty("refresh_token") String refreshToken,
		@JsonProperty("token_type") String tokenType,
		@JsonProperty("expires_in") Long expiresIn
) {

	public String resolvedAccessToken() {
		return session != null ? session.accessToken() : accessToken;
	}

	public String resolvedRefreshToken() {
		return session != null ? session.refreshToken() : refreshToken;
	}

	public String resolvedTokenType() {
		return session != null ? session.tokenType() : tokenType;
	}

	public Long resolvedExpiresIn() {
		return session != null ? session.expiresIn() : expiresIn;
	}

	public record SupabaseSession(
			@JsonProperty("access_token") String accessToken,
			@JsonProperty("refresh_token") String refreshToken,
			@JsonProperty("token_type") String tokenType,
			@JsonProperty("expires_in") Long expiresIn
	) {
	}

	public record SupabaseUser(
			String id,
			String email,
			@JsonProperty("user_metadata") Map<String, Object> userMetadata
	) {
	}

}
