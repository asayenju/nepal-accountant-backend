package com.nepalaccountant.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for AI service provider integration.
 * 
 * Stores AI provider details including the provider name, model, API key, and base URL.
 * These properties are used for invoice analysis and AI-powered feature integration.
 * 
 * Properties are populated from application configuration with prefix "app.ai".
 */
@ConfigurationProperties(prefix = "app.ai")
public record AiProviderProperties(
		String provider,
		String model,
		String apiKey,
		String baseUrl
) {
}
