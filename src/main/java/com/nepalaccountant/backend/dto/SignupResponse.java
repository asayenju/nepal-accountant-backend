package com.nepalaccountant.backend.dto;

public record SignupResponse(
		String userId,
		String email,
		boolean requiresEmailConfirmation,
		String message
) {
}
