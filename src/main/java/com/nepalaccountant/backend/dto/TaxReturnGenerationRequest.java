package com.nepalaccountant.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record TaxReturnGenerationRequest(
		@NotBlank(message = "taxpayerId is required")
		String taxpayerId,
		@NotBlank(message = "fiscalYear is required")
		String fiscalYear,
		@NotEmpty(message = "invoiceIds must contain at least one invoice")
		List<String> invoiceIds
) {
}
