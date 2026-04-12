package com.nepalaccountant.backend.controller;

import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for system health check operations.
 * Provides endpoints for monitoring and verifying service availability.
 * Base Path: /api/v1/system
 */
@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

	/**
	 * Performs a basic health check of the backend service.
	 * 
	 * Returns the current service status, name, and server timestamp. This endpoint
	 * is publicly accessible and does not require authentication, making it suitable
	 * for monitoring and load balancer health checks.
	 * 
	 * @return a map containing service status ("ok"), service name, and current timestamp
	 */
	@GetMapping("/health")
	public Map<String, Object> health() {
		return Map.of(
				"status", "ok",
				"service", "nepal-accountant-backend",
				"timestamp", OffsetDateTime.now()
		);
	}

}
