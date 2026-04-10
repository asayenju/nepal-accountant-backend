package com.nepalaccountant.backend.controller;

import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

	@GetMapping("/health")
	public Map<String, Object> health() {
		return Map.of(
				"status", "ok",
				"service", "nepal-accountant-backend",
				"timestamp", OffsetDateTime.now()
		);
	}

}
