package com.personrecipe.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Component
@ConfigurationProperties(prefix = "qiniu")
public class QiniuProperties {

	@NotBlank(message = "七牛云 access-key 不能为空")
	private String accessKey;

	@NotBlank(message = "七牛云 secret-key 不能为空")
	private String secretKey;

	@NotBlank(message = "七牛云 bucket 不能为空")
	private String bucket;

	@NotBlank(message = "七牛云访问域名不能为空")
	@Pattern(regexp = "^https?://.+", message = "七牛云访问域名必须以 http:// 或 https:// 开头")
	private String domain;

	public String normalizedDomain() {
		String value = domain.trim();
		while (value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		return value;
	}
}