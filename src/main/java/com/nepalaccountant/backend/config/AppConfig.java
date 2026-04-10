package com.nepalaccountant.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

	@Bean
	WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}

	@Bean
	WebClient supabaseWebClient(SupabaseProperties properties, WebClient.Builder builder) {
		return builder
				.baseUrl(properties.url())
				.defaultHeaders(headers -> {
					headers.setBearerAuth(properties.serviceRoleKey());
					headers.add("apikey", properties.anonKey());
				})
				.build();
	}

}
