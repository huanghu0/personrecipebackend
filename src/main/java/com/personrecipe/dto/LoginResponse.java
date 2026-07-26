package com.personrecipe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

	private String token;

	/**
	 * Token 剩余有效秒数（固定为 7 天对应的秒数）。
	 */
	private long expiresIn;

	private UserInfoVO user;
}
