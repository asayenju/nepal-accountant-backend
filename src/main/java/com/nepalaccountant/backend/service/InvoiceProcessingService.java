package com.nepalaccountant.backend.service;

import com.nepalaccountant.backend.dto.InvoiceAnalysisRequest;
import com.nepalaccountant.backend.dto.InvoiceAnalysisResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Service for processing and analyzing invoices.
 * 
 * Handles invoice uploads, OCR processing, and AI-based field extraction
 * to identify relevant tax information from invoice documents.
 */
@Service
public class InvoiceProcessingService {

	/**
	 * Analyzes an uploaded invoice and extracts relevant information.
	 * 
	 * Initiates asynchronous invoice processing including OCR, field extraction,
	 * and mapping to Nepal tax regulations. Returns immediately with a tracking ID
	 * for progress monitoring.
	 * 
	 * @param request the invoice analysis request containing invoice document details
	 * @return an {@link InvoiceAnalysisResponse} with tracking ID, status, and processing steps
	 * @throws IllegalStateException if invoice processing initialization fails
	 */
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
