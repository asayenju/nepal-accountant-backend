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

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

	private final InvoiceProcessingService invoiceProcessingService;

	public InvoiceController(InvoiceProcessingService invoiceProcessingService) {
		this.invoiceProcessingService = invoiceProcessingService;
	}

	@PostMapping("/analyze")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public InvoiceAnalysisResponse analyzeInvoice(@Valid @RequestBody InvoiceAnalysisRequest request) {
		return invoiceProcessingService.analyzeInvoice(request);
	}

}
