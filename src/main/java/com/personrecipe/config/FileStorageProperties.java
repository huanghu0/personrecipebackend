package com.personrecipe.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

	/**
	 * 本地上传根目录（相对项目运行目录或绝对路径）。
	 */
	private String uploadDir = "uploads";

	/**
	 * 对外访问前缀，如 /uploads/xxx.jpg。
	 */
	private String accessPath = "/uploads";
}
