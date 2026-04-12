package com.nepalaccountant.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Supabase integration.
 * 
 * Stores Supabase project URL, API keys, and other configuration needed for
 * authentication, database operations, and file storage.
 * 
 * Properties are populated from application configuration with prefix "app.supabase".
 */
@ConfigurationProperties(prefix = "app.supabase")
public record SupabaseProperties(
		String url,
		String anonKey,
		String serviceRoleKey,
		String invoiceBucket
) {

	/**
	 * Checks if Supabase URL is properly configured with a real project URL.
	 *
	 * @return true if URL is configured and not a placeholder value
	 */
	public boolean hasConfiguredUrl() {
		return hasText(url) && !url.contains("your-project.supabase.co");
	}

	/**
	 * Checks if Supabase anonymous API key is properly configured.
	 *
	 * @return true if anon key is configured and not a placeholder value
	 */
	public boolean hasConfiguredAnonKey() {
		return hasText(anonKey) && !"replace-me".equals(anonKey);
	}

	/**
	 * Checks if Supabase service role API key is properly configured.
	 *
	 * @return true if service role key is configured and not a placeholder value
	 */
	public boolean hasConfiguredServiceRoleKey() {
		return hasText(serviceRoleKey) && !"replace-me".equals(serviceRoleKey);
	}

	/**
	 * Checks if a string has meaningful text content.
	 * 
	 * @param value the string to check
	 * @return true if the value is not null and contains non-whitespace characters
	 */
	private boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
	}
}
