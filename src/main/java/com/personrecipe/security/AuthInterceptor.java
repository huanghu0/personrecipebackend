package com.personrecipe.security;

import com.personrecipe.common.ApiResponse;
import com.personrecipe.common.BusinessException;
import com.personrecipe.common.UserContext;
import com.personrecipe.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer ";

	private final JwtUtil jwtUtil;
	private final TokenBlacklistService tokenBlacklistService;
	private final JsonMapper jsonMapper;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			return true;
		}

		String authorization = request.getHeader(AUTHORIZATION_HEADER);
		if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
			writeUnauthorized(response, "未登录或缺少 Token");
			return false;
		}

		String token = authorization.substring(BEARER_PREFIX.length()).trim();
		if (!StringUtils.hasText(token)) {
			writeUnauthorized(response, "未登录或缺少 Token");
			return false;
		}

		try {
			Claims claims = jwtUtil.parseClaims(token);
			String jti = claims.getId();
			if (!StringUtils.hasText(jti) || tokenBlacklistService.contains(jti)) {
				writeUnauthorized(response, "登录已失效，请重新登录");
				return false;
			}
			UserContext.setUserId(Long.valueOf(claims.getSubject()));
			return true;
		} catch (BusinessException e) {
			writeUnauthorized(response, e.getMessage());
			return false;
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		UserContext.clear();
	}

	private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		jsonMapper.writeValue(response.getWriter(), ApiResponse.fail(401, message));
	}
}
