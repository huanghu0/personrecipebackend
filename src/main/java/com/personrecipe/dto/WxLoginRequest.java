package com.personrecipe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WxLoginRequest {

	/**
	 * 微信 wx.login() 返回的临时登录凭证 code。
	 */
	@NotBlank(message = "code 不能为空")
	private String code;

	/**
	 * 可选：昵称（客户端授权后传入）。
	 */
	private String nickname;

	/**
	 * 可选：头像 URL。
	 */
	private String avatarUrl;
}
