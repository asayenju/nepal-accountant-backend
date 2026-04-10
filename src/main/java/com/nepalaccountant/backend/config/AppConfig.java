package com.nepalaccountant.backend.config;

import java.net.http.HttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

	@Bean
	WebClient.Builder webClientBuilder() {
		return WebClient.builder()
				.clientConnector(new JdkClientHttpConnector(HttpClient.newHttpClient()));
	}

	@Bean
	@Qualifier("supabaseAnonWebClient")
	WebClient supabaseAnonWebClient(SupabaseProperties properties, WebClient.Builder builder) {
		return builder
				.baseUrl(properties.url())
				.defaultHeader("apikey", properties.anonKey())
				.build();
	}

}
