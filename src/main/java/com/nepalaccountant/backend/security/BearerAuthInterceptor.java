package com.nepalaccountant.backend.security;

import com.nepalaccountant.backend.dto.AuthenticatedUser;
import com.nepalaccountant.backend.exception.UnauthorizedException;
import com.nepalaccountant.backend.service.AuthSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * HTTP request interceptor for validating Bearer token authentication.
 * 
 * Intercepts incoming requests to validate and process JWT access tokens from
 * the Authorization header. Extracts authenticated user information and stores it
 * in request attributes for access by controllers and services.
 */
@Component
public class BearerAuthInterceptor implements HandlerInterceptor {

	/** Request attribute key for storing the authenticated user */
	public static final String AUTHENTICATED_USER_ATTRIBUTE = "authenticatedUser";
	
	/** Request attribute key for storing the JWT access token */
	public static final String ACCESS_TOKEN_ATTRIBUTE = "accessToken";

	private final AuthSessionService authSessionService;

	/**
	 * Constructs a BearerAuthInterceptor with the authentication session service.
	 *
	 * @param authSessionService the service for validating access tokens
	 */
	public BearerAuthInterceptor(AuthSessionService authSessionService) {
		this.authSessionService = authSessionService;
	}

	/**
	 * Validates the Bearer token in the Authorization header before handling the request.
	 * 
	 * Extracts the JWT access token from the Authorization header, validates it with Supabase,
	 * retrieves the authenticated user information, and stores both in request attributes
	 * for access by downstream handlers.
	 * 
	 * @param request the HTTP request containing the Authorization header
	 * @param response the HTTP response
	 * @param handler the handler being executed
	 * @return true to continue request processing
	 * @throws UnauthorizedException if the Authorization header is missing, invalid, or token is invalid/expired
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String authorization = request.getHeader("Authorization");
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			throw new UnauthorizedException("Missing or invalid Authorization header. Use Bearer <access_token>.");
		}

		String accessToken = authorization.substring(7).trim();
		if (accessToken.isEmpty()) {
			throw new UnauthorizedException("Bearer token is missing.");
		}

		AuthenticatedUser authenticatedUser = authSessionService.validateAccessToken(accessToken);
		request.setAttribute(AUTHENTICATED_USER_ATTRIBUTE, authenticatedUser);
		request.setAttribute(ACCESS_TOKEN_ATTRIBUTE, accessToken);
		return true;
	}

}
