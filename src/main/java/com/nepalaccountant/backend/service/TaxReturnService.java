package com.nepalaccountant.backend.service;

import com.nepalaccountant.backend.dto.TaxReturnGenerationRequest;
import com.nepalaccountant.backend.dto.TaxReturnSummaryResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TaxReturnService {

	public TaxReturnSummaryResponse generateDraftReturn(TaxReturnGenerationRequest request) {
		return new TaxReturnSummaryResponse(
				UUID.randomUUID().toString(),
				"DRAFT",
				request.fiscalYear(),
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				List.of(
						"Tax logic is not implemented yet",
						"Invoice classification still needs AI extraction and manual review support"
				)
		);
	}

}
