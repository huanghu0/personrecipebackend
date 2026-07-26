package com.personrecipe.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WeChatProperties {

	private String appId;

	private String appSecret;

	/**
	 * 为 true 时不调用微信接口，直接把 code 当作 openid（仅本地联调）。
	 */
	private boolean mockEnabled;
}
