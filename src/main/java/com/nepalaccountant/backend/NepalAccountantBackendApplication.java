package com.nepalaccountant.backend;

import com.nepalaccountant.backend.config.AiProviderProperties;
import com.nepalaccountant.backend.config.SupabaseProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = {
		SupabaseProperties.class,
		AiProviderProperties.class
})
public class NepalAccountantBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(NepalAccountantBackendApplication.class, args);
	}

}
