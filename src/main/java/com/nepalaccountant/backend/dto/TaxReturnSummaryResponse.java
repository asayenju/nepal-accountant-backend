package com.nepalaccountant.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record TaxReturnSummaryResponse(
		String returnId,
		String status,
		String fiscalYear,
		BigDecimal estimatedTaxableAmount,
		BigDecimal estimatedTaxDue,
		List<String> warnings
) {
}
