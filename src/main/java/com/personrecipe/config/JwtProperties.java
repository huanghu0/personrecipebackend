package com.personrecipe.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	/**
	 * HMAC 签名密钥，生产环境务必替换。
	 */
	private String secret;

	/**
	 * Token 有效天数，默认 7 天。
	 */
	private int expireDays = 7;
}
