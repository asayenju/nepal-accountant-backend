package com.nepalaccountant.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record BusinessResponse(
		String id,
		@JsonProperty("user_id") String userId,
		String name,
		@JsonProperty("pan_number") String panNumber,
		@JsonProperty("vat_number") String vatNumber,
		@JsonProperty("registration_number") String registrationNumber,
		String address,
		String industry,
		@JsonProperty("is_vat_registered") Boolean isVatRegistered,
		@JsonProperty("created_at") OffsetDateTime createdAt,
		@JsonProperty("updated_at") OffsetDateTime updatedAt
) {
}
