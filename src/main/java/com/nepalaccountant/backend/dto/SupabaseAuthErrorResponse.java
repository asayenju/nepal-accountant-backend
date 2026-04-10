package com.nepalaccountant.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SupabaseAuthErrorResponse(
		String error,
		@JsonProperty("error_code") String errorCode,
		String msg,
		String message,
		@JsonProperty("error_description") String errorDescription
) {
}
