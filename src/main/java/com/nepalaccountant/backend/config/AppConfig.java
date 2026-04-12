package com.nepalaccountant.backend.config;

import java.net.http.HttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Application configuration for HTTP clients and external service integration.
 * 
 * Configures WebClient instances for communicating with Supabase and other
 * external services.
 */
@Configuration
public class AppConfig {

	/**
	 * Creates a WebClient.Builder configured for reactive HTTP requests.
	 * 
	 * Initializes the builder with Java's HttpClient for handling HTTP requests.
	 * 
	 * @return a configured {@link WebClient.Builder}
	 */
	@Bean
	WebClient.Builder webClientBuilder() {
		return WebClient.builder()
				.clientConnector(new JdkClientHttpConnector(HttpClient.newHttpClient()));
	}

	/**
	 * Creates a WebClient configured for Supabase API requests.
	 * 
	 * Configures the client with Supabase base URL and API key header for anonymous requests.
	 * This client is used for all Supabase REST API and authentication API calls.
	 * 
	 * @param properties the Supabase configuration properties
	 * @param builder the WebClient.Builder to configure
	 * @return a configured WebClient for Supabase requests
	 */
	@Bean
	@Qualifier("supabaseAnonWebClient")
	WebClient supabaseAnonWebClient(SupabaseProperties properties, WebClient.Builder builder) {
		return builder
				.baseUrl(properties.url())
				.defaultHeader("apikey", properties.anonKey())
				.build();
	}

}
