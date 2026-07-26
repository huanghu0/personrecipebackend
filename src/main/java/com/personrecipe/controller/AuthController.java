package com.personrecipe.controller;

import com.personrecipe.common.ApiResponse;
import com.personrecipe.common.BusinessException;
import com.personrecipe.dto.LoginResponse;
import com.personrecipe.dto.WxLoginRequest;
import com.personrecipe.security.AuthInterceptor;
import com.personrecipe.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	/**
	 * 微信小程序登录：用 wx.login 的 code 换 JWT。
	 * 首次登录自动注册用户。
	 */
	@PostMapping("/wx-login")
	public ApiResponse<LoginResponse> wxLogin(@Valid @RequestBody WxLoginRequest request) {
		return ApiResponse.ok(authService.wxLogin(request));
	}

	/**
	 * 退出登录：作废当前 Token（需携带 Authorization）。
	 */
	@PostMapping("/logout")
	public ApiResponse<Void> logout(HttpServletRequest request) {
		authService.logout(extractBearerToken(request));
		return ApiResponse.ok(null);
	}

	private String extractBearerToken(HttpServletRequest request) {
		String authorization = request.getHeader(AuthInterceptor.AUTHORIZATION_HEADER);
		if (!StringUtils.hasText(authorization) || !authorization.startsWith(AuthInterceptor.BEARER_PREFIX)) {
			throw new BusinessException(401, "未登录或缺少 Token");
		}
		String token = authorization.substring(AuthInterceptor.BEARER_PREFIX.length()).trim();
		if (!StringUtils.hasText(token)) {
			throw new BusinessException(401, "未登录或缺少 Token");
		}
		return token;
	}
}
