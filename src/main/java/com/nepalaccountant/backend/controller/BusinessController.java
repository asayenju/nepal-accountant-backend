package com.nepalaccountant.backend.controller;

import com.nepalaccountant.backend.dto.AuthenticatedUser;
import com.nepalaccountant.backend.dto.BusinessCreateRequest;
import com.nepalaccountant.backend.dto.BusinessResponse;
import com.nepalaccountant.backend.dto.BusinessUpdateRequest;
import com.nepalaccountant.backend.exception.UnauthorizedException;
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

/**
 * REST Controller for managing businesses of a user.
 * Provides endpoints for CRUD operations on business records.
 * Base Path: /api/v1/businesses
 * 
 * All endpoints except health checks require bearer token authentication.
 */
@RestController
@RequestMapping("/api/v1/businesses")
public class BusinessController {
	private final BusinessService businessService;

	/**
	 * Constructs a BusinessController with the provided BusinessService.
	 *
	 * @param businessService the business service to be injected
	 */
	public BusinessController(BusinessService businessService) {
		this.businessService = businessService;
	}

	/**
	 * Register an existing business in our platform for a user
	 * 
	 * Extracts the user ID and access token from the incoming HTTP request headers
	 * before sending it to business service
	 * 
	 * @param request the business creation payload — see
 	 * {@link BusinessCreateRequest} for field details
	 * 
	 * @param httpServletRequest the raw HTTP request used to extract 
	 * authenticated user and Supabase access token
	 * 
	 * @return the created business details including generated ID and timestamps - see
	 * {@link BusinessResponse} for filed details
	 * 
	 * @throws IllegalStateException if supabase fails to persist the business or 
	 * the user session is invalid
	*/
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

	/**
	 * Returns all businesses of a user
	 * 
	 * Extracts the user ID and access token from the incoming HTTP request headers
	 * before sending it to business service
	 * 
	 * @param httpServletRequest the raw HTTP request used to extract 
	 * authenticated user and Supabase access token
	 * 
	 * @return List of Business Response - see
	 * {@link BusinessResponse} for more details
	*/
	@GetMapping
	public List<BusinessResponse> getMyBusinesses(HttpServletRequest httpServletRequest) {
		return businessService.getBusinessesForUser(
				authenticatedUser(httpServletRequest),
				accessToken(httpServletRequest)
		);
	}

	/**
	 * Returns a business that matches businessId of a user
	 * 
	 * Extracts the user ID and access token from the incoming HTTP request headers
	 * before sending it to business service
	 * 
	 * @param businessId the unique identifier of the business
	 * 
	 * @param httpServletRequest the raw HTTP request used to extract 
	 * authenticated user and Supabase access token
	 * 
	 * @return a {@link BusinessResponse} containing the business data
	 * @throws BusinessNotFoundException if no business matches the given ID
	 * @throws UnauthorizedException     if the user is not authenticated
	*/

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

	/**
	 * Updates a business record with provided details.
	 * 
	 * Extracts the user ID and access token from the incoming HTTP request headers
	 * before sending it to business service
	 * 
	 * @param businessId the unique identifier of the business to update
	 * 
	 * @param request the business update payload containing optional fields to update
	 *                 - see {@link BusinessUpdateRequest} for field details
	 * 
	 * @param httpServletRequest the raw HTTP request used to extract 
	 * authenticated user and Supabase access token
	 * 
	 * @return the updated {@link BusinessResponse} containing the new business data
	 * @throws BusinessNotFoundException if no business matches the given ID
	 * @throws UnauthorizedException     if the user is not authenticated
	 * @throws IllegalStateException     if update fails or no fields are provided
	 */
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

	/**
	 * Deletes a business record for the authenticated user.
	 * 
	 * Extracts the user ID and access token from the incoming HTTP request headers
	 * before sending it to business service
	 * 
	 * @param businessId the unique identifier of the business to delete
	 * 
	 * @param httpServletRequest the raw HTTP request used to extract 
	 * authenticated user and Supabase access token
	 * 
	 * @throws BusinessNotFoundException if no business matches the given ID
	 * @throws UnauthorizedException     if the user is not authenticated
	 * @throws IllegalStateException     if deletion fails
	 */
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

	/**
	 * Extracts authenticated user set by BearerAuthInterceptor from request attributes.
	 *
	 * @param httpServletRequest the HTTP request containing authenticated user attribute
	 * @return the {@link AuthenticatedUser} containing user ID and email
	 */
	private AuthenticatedUser authenticatedUser(HttpServletRequest httpServletRequest) {
		return (AuthenticatedUser) httpServletRequest.getAttribute(BearerAuthInterceptor.AUTHENTICATED_USER_ATTRIBUTE);
	}

	/**
	 * Extracts JWT access token set by BearerAuthInterceptor from request attributes.
	 *
	 * @param httpServletRequest the HTTP request containing access token attribute
	 * @return the JWT access token string
	 */
	private String accessToken(HttpServletRequest httpServletRequest) {
		return (String) httpServletRequest.getAttribute(BearerAuthInterceptor.ACCESS_TOKEN_ATTRIBUTE);
	}

}
