package com.nepalaccountant.backend.dto;

public record BusinessUpdateRequest(
		String name,
		String panNumber,
		String vatNumber,
		String registrationNumber,
		String address,
		String industry,
		Boolean isVatRegistered
) {
}
