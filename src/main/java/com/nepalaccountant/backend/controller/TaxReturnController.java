package com.nepalaccountant.backend.controller;

import com.nepalaccountant.backend.dto.TaxReturnGenerationRequest;
import com.nepalaccountant.backend.dto.TaxReturnSummaryResponse;
import com.nepalaccountant.backend.service.TaxReturnService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tax-returns")
public class TaxReturnController {

	private final TaxReturnService taxReturnService;

	public TaxReturnController(TaxReturnService taxReturnService) {
		this.taxReturnService = taxReturnService;
	}

	@PostMapping("/generate")
	public TaxReturnSummaryResponse generate(@Valid @RequestBody TaxReturnGenerationRequest request) {
		return taxReturnService.generateDraftReturn(request);
	}

}
