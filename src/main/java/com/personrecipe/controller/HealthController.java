package com.personrecipe.controller;

import com.personrecipe.common.ApiResponse;
import com.personrecipe.common.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

	/**
	 * 需登录后访问，可用于验证 Token 是否生效。
	 */
	@GetMapping("/health")
	public ApiResponse<Map<String, Object>> health() {
		return ApiResponse.ok(Map.of(
				"status", "UP",
				"application", "personpecipebackend",
				"userId", UserContext.requireUserId()
		));
	}
}
