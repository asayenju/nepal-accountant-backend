package com.nepalaccountant.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InvoiceAnalysisRequest(
		@NotBlank(message = "invoiceImageBase64 is required")
		@Size(max = 5_000_000, message = "invoiceImageBase64 is too large for the initial API contract")
		String invoiceImageBase64,
		@NotBlank(message = "fileName is required")
		String fileName,
		@NotBlank(message = "contentType is required")
		String contentType
) {
}
