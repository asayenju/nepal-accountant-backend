package com.nepalaccountant.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record BusinessCreateRequest(
		@NotBlank(message = "name is required")
		String name,
		@NotBlank(message = "panNumber is required")
		String panNumber,
		String vatNumber,
		String registrationNumber,
		String address,
		String industry,
		Boolean isVatRegistered
) {
}
