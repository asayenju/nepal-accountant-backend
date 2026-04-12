package com.nepalaccountant.backend.controller;

import com.nepalaccountant.backend.dto.TaxReturnGenerationRequest;
import com.nepalaccountant.backend.dto.TaxReturnSummaryResponse;
import com.nepalaccountant.backend.service.TaxReturnService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for tax return generation operations.
 * Handles tax return creation and management requests.
 * Base Path: /api/v1/tax-returns
 */
@RestController
@RequestMapping("/api/v1/tax-returns")
public class TaxReturnController {

	private final TaxReturnService taxReturnService;

	/**
	 * Constructs a TaxReturnController with the provided TaxReturnService.
	 *
	 * @param taxReturnService the tax return service to be injected
	 */
	public TaxReturnController(TaxReturnService taxReturnService) {
		this.taxReturnService = taxReturnService;
	}

	/**
	 * Generates a draft tax return for a business in a specific fiscal year.
	 * 
	 * Creates a draft tax return based on the provided business and fiscal year information.
	 * The return is initially in DRAFT status and can be reviewed and modified before submission.
	 * 
	 * @param request the tax return generation request containing business and fiscal year details
	 *                 - see {@link TaxReturnGenerationRequest} for field details
	 * @return a {@link TaxReturnSummaryResponse} containing return ID, status, and summary information
	 * @throws IllegalStateException if tax return generation fails
	 */
	@PostMapping("/generate")
	public TaxReturnSummaryResponse generate(@Valid @RequestBody TaxReturnGenerationRequest request) {
		return taxReturnService.generateDraftReturn(request);
	}

}
