package com.nepalaccountant.backend.controller;

import com.nepalaccountant.backend.dto.AuthLoginRequest;
import com.nepalaccountant.backend.dto.AuthSignupRequest;
import com.nepalaccountant.backend.dto.LoginResponse;
import com.nepalaccountant.backend.dto.SignupResponse;
import com.nepalaccountant.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public SignupResponse signup(@Valid @RequestBody AuthSignupRequest request) {
		return authService.signup(request);
	}

	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody AuthLoginRequest request) {
		return authService.login(request);
	}

}
