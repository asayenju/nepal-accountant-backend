package com.nepalaccountant.backend.controller;

import com.nepalaccountant.backend.dto.InvoiceAnalysisRequest;
import com.nepalaccountant.backend.dto.InvoiceAnalysisResponse;
import com.nepalaccountant.backend.service.InvoiceProcessingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for invoice processing operations.
 * Handles invoice analysis and extraction requests.
 * Base Path: /api/v1/invoices
 */
@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

	private final InvoiceProcessingService invoiceProcessingService;

	/**
	 * Constructs an InvoiceController with the provided InvoiceProcessingService.
	 *
	 * @param invoiceProcessingService the invoice processing service to be injected
	 */
	public InvoiceController(InvoiceProcessingService invoiceProcessingService) {
		this.invoiceProcessingService = invoiceProcessingService;
	}

	/**
	 * Analyzes an uploaded invoice and extracts relevant information.
	 * 
	 * Processes the invoice through OCR and AI-based field extraction to identify
	 * relevant tax information. Returns an analysis response with a tracking ID for
	 * asynchronous processing.
	 * 
	 * @param request the invoice analysis request containing invoice details
	 *                 - see {@link InvoiceAnalysisRequest} for field details
	 * @return an {@link InvoiceAnalysisResponse} containing analysis ID, status, and processing steps
	 * @throws IllegalStateException if invoice processing fails
	 */
	@PostMapping("/analyze")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public InvoiceAnalysisResponse analyzeInvoice(@Valid @RequestBody InvoiceAnalysisRequest request) {
		return invoiceProcessingService.analyzeInvoice(request);
	}

}
