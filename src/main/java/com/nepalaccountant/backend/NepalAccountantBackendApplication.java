package com.nepalaccountant.backend;

import com.nepalaccountant.backend.config.AiProviderProperties;
import com.nepalaccountant.backend.config.SupabaseProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Main Spring Boot application entry point for Nepal Accountant Backend.
 * 
 * This application provides REST APIs for managing business accounting, invoices,
 * and tax returns for users in Nepal. It integrates with Supabase for authentication
 * and data persistence, and AI providers for invoice analysis.
 * 
 * Configuration properties are scanned from SupabaseProperties and AiProviderProperties classes.
 */
@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = {
		SupabaseProperties.class,
		AiProviderProperties.class
})
public class NepalAccountantBackendApplication {

	/**
	 * Main method to start the Spring Boot application.
	 *
	 * @param args command-line arguments (typically empty)
	 */
	public static void main(String[] args) {
		SpringApplication.run(NepalAccountantBackendApplication.class, args);
	}

}
