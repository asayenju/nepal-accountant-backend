package com.nepalaccountant.backend.security;

import com.nepalaccountant.backend.dto.AuthenticatedUser;
import com.nepalaccountant.backend.exception.UnauthorizedException;
import com.nepalaccountant.backend.service.AuthSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class BearerAuthInterceptor implements HandlerInterceptor {

	public static final String AUTHENTICATED_USER_ATTRIBUTE = "authenticatedUser";
	public static final String ACCESS_TOKEN_ATTRIBUTE = "accessToken";

	private final AuthSessionService authSessionService;

	public BearerAuthInterceptor(AuthSessionService authSessionService) {
		this.authSessionService = authSessionService;
	}

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
