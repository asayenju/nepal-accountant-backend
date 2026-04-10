package com.nepalaccountant.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceAnalysisResponse(
		String invoiceId,
		String processingStatus,
		String supplierName,
		LocalDate invoiceDate,
		BigDecimal totalAmount,
		String currency,
		List<String> nextSteps
) {
}
