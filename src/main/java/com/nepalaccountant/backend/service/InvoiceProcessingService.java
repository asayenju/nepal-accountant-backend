package com.nepalaccountant.backend.service;

import com.nepalaccountant.backend.dto.InvoiceAnalysisRequest;
import com.nepalaccountant.backend.dto.InvoiceAnalysisResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class InvoiceProcessingService {

	public InvoiceAnalysisResponse analyzeInvoice(InvoiceAnalysisRequest request) {
		return new InvoiceAnalysisResponse(
				UUID.randomUUID().toString(),
				"QUEUED",
				"Pending AI extraction",
				LocalDate.now(),
				BigDecimal.ZERO,
				"NPR",
				List.of(
						"Persist the image in Supabase Storage",
						"Run OCR + invoice field extraction",
						"Map extracted values to Nepal tax rules"
				)
		);
	}

}
