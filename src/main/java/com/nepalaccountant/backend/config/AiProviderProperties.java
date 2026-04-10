package com.nepalaccountant.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ai")
public record AiProviderProperties(
		String provider,
		String model,
		String apiKey,
		String baseUrl
) {
}
