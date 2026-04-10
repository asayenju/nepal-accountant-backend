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

@Service
public class BusinessService {

	private final SupabaseProperties supabaseProperties;
	private final WebClient supabaseAnonWebClient;

	public BusinessService(
			SupabaseProperties supabaseProperties,
			@Qualifier("supabaseAnonWebClient") WebClient supabaseAnonWebClient
	) {
		this.supabaseProperties = supabaseProperties;
		this.supabaseAnonWebClient = supabaseAnonWebClient;
	}

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
					.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
							.map(errorBody -> new IllegalStateException("Failed to fetch business: " + errorBody)))
					.bodyToFlux(BusinessResponse.class)
					.collectList()
					.block();

			if (businesses == null || businesses.isEmpty()) {
				throw new ResourceNotFoundException("Business not found for the authenticated user.");
			}

			return businesses.get(0);
		} catch (WebClientRequestException exception) {
			throw new IllegalStateException("Failed to reach Supabase while fetching business.");
		}
	}

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

	private void validateBusinessConfig() {
		if (!supabaseProperties.hasConfiguredUrl()) {
			throw new IllegalStateException("SUPABASE_URL is not configured. Set it before using business endpoints.");
		}
		if (!supabaseProperties.hasConfiguredAnonKey()) {
			throw new IllegalStateException("SUPABASE_ANON_KEY is not configured. Set it before using business endpoints.");
		}
	}

	private String normalizeBlank(String value) {
		return hasText(value) ? value.trim() : null;
	}

	private void putIfPresent(Map<String, Object> payload, String key, String value) {
		if (value != null) {
			payload.put(key, normalizeBlank(value));
		}
	}

	private boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
	}

}
