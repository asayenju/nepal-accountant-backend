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

/**
 * REST Controller for user authentication operations.
 * Handles user signup and login requests.
 * Base Path: /api/v1/auth
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;

	/**
	 * Constructs an AuthController with the provided AuthService.
	 *
	 * @param authService the authentication service to be injected
	 */
	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	/**
	 * Registers a new user with email and password.
	 * 
	 * Creates a new user account via Supabase authentication. If email confirmation
	 * is enabled in Supabase, the user will need to verify their email before logging in.
	 * 
	 * @param request the signup request containing email, password, and optional user profile details
	 *                 - see {@link AuthSignupRequest} for field details
	 * @return a {@link SignupResponse} containing user ID, email, confirmation status, and a message
	 * @throws IllegalStateException if Supabase signup fails or returns invalid data
	 * @throws SupabaseAuthException if Supabase returns an authentication error (e.g., email already exists)
	 */
	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public SignupResponse signup(@Valid @RequestBody AuthSignupRequest request) {
		return authService.signup(request);
	}

	/**
	 * Authenticates a user with email and password.
	 * 
	 * Validates user credentials against Supabase authentication. Upon successful authentication,
	 * returns an access token that can be used for subsequent API requests.
	 * 
	 * @param request the login request containing email and password
	 *                 - see {@link AuthLoginRequest} for field details
	 * @return a {@link LoginResponse} containing access token, refresh token, user ID, email, and session details
	 * @throws IllegalStateException if Supabase login fails or returns invalid data
	 * @throws SupabaseAuthException if Supabase returns an authentication error (e.g., invalid credentials)
	 */
	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody AuthLoginRequest request) {
		return authService.login(request);
	}

}
