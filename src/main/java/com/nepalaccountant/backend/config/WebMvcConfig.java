package com.nepalaccountant.backend.config;

import com.nepalaccountant.backend.security.BearerAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Web MVC configuration for request interceptors and other web settings.
 * 
 * Configures the Bearer token authentication interceptor to validate JWT tokens
 * on incoming requests to protected endpoints.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final BearerAuthInterceptor bearerAuthInterceptor;

	/**
	 * Constructs WebMvcConfig with the bearer auth interceptor.
	 *
	 * @param bearerAuthInterceptor the interceptor to validate Bearer tokens
	 */
	public WebMvcConfig(BearerAuthInterceptor bearerAuthInterceptor) {
		this.bearerAuthInterceptor = bearerAuthInterceptor;
	}

	/**
	 * Registers the BearerAuthInterceptor for API endpoints.
	 * 
	 * Applies the interceptor to all /api/v1/** endpoints except public endpoints like
	 * signup, login, and health checks.
	 * 
	 * @param registry the interceptor registry to add interceptors to
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(bearerAuthInterceptor)
				.addPathPatterns("/api/v1/**")
				.excludePathPatterns(
						"/api/v1/auth/signup",
						"/api/v1/auth/login",
						"/api/v1/system/health"
				);
	}

}
