package com.nepalaccountant.backend.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiErrorResponse(
		String code,
		String message,
		List<String> details,
		OffsetDateTime timestamp
) {
}
