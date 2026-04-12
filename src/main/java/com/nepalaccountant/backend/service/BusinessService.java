package com.nepalaccountant.backend.service;

import com.nepalaccountant.backend.config.SupabaseProperties;
import com.nepalaccountant.backend.dto.AuthenticatedUser;
import com.nepalaccountant.backend.dto.BusinessCreateRequest;
import com.nepalaccountant.backend.dto.BusinessResponse;
import com.nepalaccountant.backend.dto.BusinessUpdateRequest;
import com.nepalaccountant.backend.exception.ResourceNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/**
 * Service for managing business data operations with Supabase.
 * 
 * Provides CRUD (Create, Read, Update, Delete) operations for business records.
 * All operations require user authentication and proper Supabase access tokens.
 */
@Service
public class BusinessService {

	private final SupabaseProperties supabaseProperties;
	private final WebClient supabaseAnonWebClient;

	/**
	 * Constructs a BusinessService with Supabase client and properties.
	 *
	 * @param supabaseProperties contains Supabase configuration (URL, keys)
	 * @param supabaseAnonWebClient the WebClient configured for Supabase anonymous requests
	 */
	public BusinessService(
			SupabaseProperties supabaseProperties,
			@Qualifier("supabaseAnonWebClient") WebClient supabaseAnonWebClient
	) {
		this.supabaseProperties = supabaseProperties;
		this.supabaseAnonWebClient = supabaseAnonWebClient;
	}

	/**
	 * Creates a new business record for an authenticated user.
	 * 
	 * Persists a new business to the Supabase database with the provided details.
	 * The business is associated with the authenticated user's ID.
	 * 
	 * @param request the business creation details including name, PAN, VAT, etc.
	 * @param authenticatedUser the authenticated user creating the business
	 * @param accessToken the Supabase access token for authorization
	 * @return a {@link BusinessResponse} with the created business details including generated ID
	 * @throws IllegalStateException if Supabase configuration is missing or creation fails
	 */
	public BusinessResponse createBusiness(
			BusinessCreateRequest request,
			AuthenticatedUser authenticatedUser,
			String accessToken
	) {
		validateBusinessConfig();

		Map<String, Object> payload = new HashMap<>();
		payload.put("user_id", authenticatedUser.id());
		payload.put("name", request.name().trim());
		payload.put("pan_number", request.panNumber().trim());
		payload.put("vat_number", normalizeBlank(request.vatNumber()));
		payload.put("registration_number", normalizeBlank(request.registrationNumber()));
		payload.put("address", normalizeBlank(request.address()));
		payload.put("industry", normalizeBlank(request.industry()));
		payload.put("is_vat_registered", Boolean.TRUE.equals(request.isVatRegistered()));

		try {
			BusinessResponse response = supabaseAnonWebClient.post()
					.uri("/rest/v1/businesses")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.header("Prefer", "return=representation")
					.bodyValue(List.of(payload))
					.retrieve()
					.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
							.map(errorBody -> new IllegalStateException("Failed to create business: " + errorBody)))
					.bodyToFlux(BusinessResponse.class)
					.singleOrEmpty()
					.block();

			if (response == null) {
				throw new IllegalStateException("Business creation did not return a created record.");
			}

			return response;
		} catch (WebClientRequestException exception) {
			throw new IllegalStateException("Failed to reach Supabase while creating business.");
		}
	}

	/**
	 * Retrieves all businesses owned by an authenticated user.
	 * 
	 * Fetches all business records associated with the authenticated user, ordered by creation date.
	 * 
	 * @param authenticatedUser the authenticated user requesting their businesses
	 * @param accessToken the Supabase access token for authorization
	 * @return a list of {@link BusinessResponse} objects for the user's businesses
	 * @throws IllegalStateException if Supabase configuration is missing or fetch fails
	 */
	public List<BusinessResponse> getBusinessesForUser(AuthenticatedUser authenticatedUser, String accessToken) {
		validateBusinessConfig();

		try {
			return supabaseAnonWebClient.get()
					.uri(uriBuilder -> uriBuilder
							.path("/rest/v1/businesses")
							.queryParam("user_id", "eq." + authenticatedUser.id())
							.queryParam("order", "created_at.desc")
							.build())
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.retrieve()
					.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
							.map(errorBody -> new IllegalStateException("Failed to fetch businesses: " + errorBody)))
					.bodyToFlux(BusinessResponse.class)
					.collectList()
					.block();
		} catch (WebClientRequestException exception) {
			throw new IllegalStateException("Failed to reach Supabase while fetching businesses.");
		}
	}

	/**
	 * Retrieves a specific business by ID for an authenticated user.
	 * 
	 * Fetches a business record matching the provided business ID and ensures
	 * the business belongs to the authenticated user.
	 * 
	 * @param businessId the unique identifier of the business to retrieve
	 * @param authenticatedUser the authenticated user requesting the business
	 * @param accessToken the Supabase access token for authorization
	 * @return a {@link BusinessResponse} containing the business details
	 * @throws ResourceNotFoundException if no business matches the given ID
	 * @throws IllegalStateException if Supabase configuration is missing or fetch fails
	 */
	public BusinessResponse getBusinessById(
			String businessId,
			AuthenticatedUser authenticatedUser,
			String accessToken
	) {
		validateBusinessConfig();

		try {
			List<BusinessResponse> businesses = supabaseAnonWebClient.get()
					.uri(uriBuilder -> uriBuilder
							.path("/rest/v1/businesses")
							.queryParam("id", "eq." + businessId)
							.queryParam("user_id", "eq." + authenticatedUser.id())
							.queryParam("limit", 1)
							.build())
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.retrieve()
					.onStatus(HttpStatusCode::isError, response ->
							response.bodyToMono(String.class)
									.map(body -> new IllegalStateException(
											"Supabase error while fetching business: " + body)))
					.bodyToFlux(BusinessResponse.class)
					.collectList()
					.block();

			if (businesses == null || businesses.isEmpty()) {
				throw new ResourceNotFoundException(
						"Business not found for user: " + authenticatedUser.id()
				);
			}

			return businesses.get(0);

		} catch (WebClientRequestException ex) {
			throw new IllegalStateException(
					"Failed to reach Supabase while fetching business", ex
			);
		}
	}

	/**
	 * Updates an existing business record with new information.
	 * 
	 * Updates only the fields provided in the request. At least one field must be provided.
	 * Verifies the business belongs to the authenticated user before updating.
	 * 
	 * @param businessId the unique identifier of the business to update
	 * @param request the update payload containing fields to modify
	 * @param authenticatedUser the authenticated user performing the update
	 * @param accessToken the Supabase access token for authorization
	 * @return a {@link BusinessResponse} with the updated business details
	 * @throws ResourceNotFoundException if no business matches the given ID
	 * @throws IllegalStateException if Supabase configuration is missing, update fails, or no fields provided
	 */
	public BusinessResponse updateBusiness(
			String businessId,
			BusinessUpdateRequest request,
			AuthenticatedUser authenticatedUser,
			String accessToken
	) {
		validateBusinessConfig();

		Map<String, Object> payload = new HashMap<>();
		putIfPresent(payload, "name", request.name());
		putIfPresent(payload, "pan_number", request.panNumber());
		putIfPresent(payload, "vat_number", request.vatNumber());
		putIfPresent(payload, "registration_number", request.registrationNumber());
		putIfPresent(payload, "address", request.address());
		putIfPresent(payload, "industry", request.industry());
		if (request.isVatRegistered() != null) {
			payload.put("is_vat_registered", request.isVatRegistered());
		}

		if (payload.isEmpty()) {
			throw new IllegalStateException("At least one business field must be provided for update.");
		}

		try {
			BusinessResponse response = supabaseAnonWebClient.patch()
					.uri(uriBuilder -> uriBuilder
							.path("/rest/v1/businesses")
							.queryParam("id", "eq." + businessId)
							.queryParam("user_id", "eq." + authenticatedUser.id())
							.build())
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.header("Prefer", "return=representation")
					.bodyValue(payload)
					.retrieve()
					.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
							.map(errorBody -> new IllegalStateException("Failed to update business: " + errorBody)))
					.bodyToFlux(BusinessResponse.class)
					.singleOrEmpty()
					.block();

			if (response == null) {
				throw new ResourceNotFoundException("Business not found for the authenticated user.");
			}

			return response;
		} catch (WebClientRequestException exception) {
			throw new IllegalStateException("Failed to reach Supabase while updating business.");
		}
	}

	/**
	 * Deletes a business record for an authenticated user.
	 * 
	 * Removes the business from the database. Verifies the business belongs to
	 * the authenticated user before deletion.
	 * 
	 * @param businessId the unique identifier of the business to delete
	 * @param authenticatedUser the authenticated user performing the deletion
	 * @param accessToken the Supabase access token for authorization
	 * @throws ResourceNotFoundException if no business matches the given ID
	 * @throws IllegalStateException if Supabase configuration is missing or deletion fails
	 */
	public void deleteBusiness(
			String businessId,
			AuthenticatedUser authenticatedUser,
			String accessToken
	) {
		validateBusinessConfig();

		try {
			supabaseAnonWebClient.delete()
					.uri(uriBuilder -> uriBuilder
							.path("/rest/v1/businesses")
							.queryParam("id", "eq." + businessId)
							.queryParam("user_id", "eq." + authenticatedUser.id())
							.build())
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.header("Prefer", "return=minimal")
					.retrieve()
					.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
							.map(errorBody -> new IllegalStateException("Failed to delete business: " + errorBody)))
					.toBodilessEntity()
					.block();
		} catch (WebClientRequestException exception) {
			throw new IllegalStateException("Failed to reach Supabase while deleting business.");
		}
	}

	/**
	 * Validates that Supabase business operations are properly configured.
	 * 
	 * Checks that SUPABASE_URL and SUPABASE_ANON_KEY are configured with real values.
	 * 
	 * @throws IllegalStateException if required Supabase properties are not configured
	 */
	private void validateBusinessConfig() {
		if (!supabaseProperties.hasConfiguredUrl()) {
			throw new IllegalStateException("SUPABASE_URL is not configured. Set it before using business endpoints.");
		}
		if (!supabaseProperties.hasConfiguredAnonKey()) {
			throw new IllegalStateException("SUPABASE_ANON_KEY is not configured. Set it before using business endpoints.");
		}
	}

	/**
	 * Normalizes a string value by trimming whitespace and converting empty strings to null.
	 * 
	 * @param value the string to normalize
	 * @return the trimmed value or null if the original value is empty or whitespace-only
	 */
	private String normalizeBlank(String value) {
		return hasText(value) ? value.trim() : null;
	}

	/**
	 * Adds a key-value pair to a map only if the value is not null.
	 * 
	 * Useful for building partial update payloads where only some fields are provided.
	 * 
	 * @param payload the map to add the key-value pair to
	 * @param key the key to add
	 * @param value the value to add (only added if not null)
	 */
	private void putIfPresent(Map<String, Object> payload, String key, String value) {
		if (value != null) {
			payload.put(key, normalizeBlank(value));
		}
	}

	/**
	 * Checks if a string has meaningful text content.
	 * 
	 * @param value the string to check
	 * @return true if the value is not null and contains non-whitespace characters, false otherwise
	 */
	private boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
	}

}
