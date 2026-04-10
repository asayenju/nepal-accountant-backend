package com.nepalaccountant.backend.dto;

public record LoginResponse(
		String userId,
		String email,
		String accessToken,
		String refreshToken,
		String tokenType,
		Long expiresIn,
		String message
) {
}
