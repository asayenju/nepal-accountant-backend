package com.nepalaccountant.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.supabase")
public record SupabaseProperties(
		String url,
		String anonKey,
		String serviceRoleKey,
		String invoiceBucket
) {

	public boolean hasConfiguredUrl() {
		return hasText(url) && !url.contains("your-project.supabase.co");
	}

	public boolean hasConfiguredAnonKey() {
		return hasText(anonKey) && !"replace-me".equals(anonKey);
	}

	public boolean hasConfiguredServiceRoleKey() {
		return hasText(serviceRoleKey) && !"replace-me".equals(serviceRoleKey);
	}

	private boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
	}
}
