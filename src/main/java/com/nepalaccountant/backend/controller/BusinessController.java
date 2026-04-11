package com.nepalaccountant.backend.controller;

import com.nepalaccountant.backend.dto.AuthenticatedUser;
import com.nepalaccountant.backend.dto.BusinessCreateRequest;
import com.nepalaccountant.backend.dto.BusinessResponse;
import com.nepalaccountant.backend.dto.BusinessUpdateRequest;
import com.nepalaccountant.backend.security.BearerAuthInterceptor;
import com.nepalaccountant.backend.service.BusinessService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/businesses")
public class BusinessController {

	private final BusinessService businessService;

	public BusinessController(BusinessService businessService) {
		this.businessService = businessService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BusinessResponse createBusiness(
			@Valid @RequestBody BusinessCreateRequest request,
			HttpServletRequest httpServletRequest
	) {
		return businessService.createBusiness(
				request,
				authenticatedUser(httpServletRequest),
				accessToken(httpServletRequest)
		);
	}

	@GetMapping
	public List<BusinessResponse> getMyBusinesses(HttpServletRequest httpServletRequest) {
		return businessService.getBusinessesForUser(
				authenticatedUser(httpServletRequest),
				accessToken(httpServletRequest)
		);
	}

	@GetMapping("/{businessId}")
	public BusinessResponse getBusinessById(
			@PathVariable String businessId,
			HttpServletRequest httpServletRequest
	) {
		return businessService.getBusinessById(
				businessId,
				authenticatedUser(httpServletRequest),
				accessToken(httpServletRequest)
		);
	}

	@PatchMapping("/{businessId}")
	public BusinessResponse updateBusiness(
			@PathVariable String businessId,
			@RequestBody BusinessUpdateRequest request,
			HttpServletRequest httpServletRequest
	) {
		return businessService.updateBusiness(
				businessId,
				request,
				authenticatedUser(httpServletRequest),
				accessToken(httpServletRequest)
		);
	}

	@DeleteMapping("/{businessId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteBusiness(
			@PathVariable String businessId,
			HttpServletRequest httpServletRequest
	) {
		businessService.deleteBusiness(
				businessId,
				authenticatedUser(httpServletRequest),
				accessToken(httpServletRequest)
		);
	}

	private AuthenticatedUser authenticatedUser(HttpServletRequest httpServletRequest) {
		return (AuthenticatedUser) httpServletRequest.getAttribute(BearerAuthInterceptor.AUTHENTICATED_USER_ATTRIBUTE);
	}

	private String accessToken(HttpServletRequest httpServletRequest) {
		return (String) httpServletRequest.getAttribute(BearerAuthInterceptor.ACCESS_TOKEN_ATTRIBUTE);
	}

}
