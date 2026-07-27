package com.personrecipe.config;

import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QiniuConfig {

	@Bean
	public Auth qiniuAuth(QiniuProperties properties) {
		return Auth.create(properties.getAccessKey(), properties.getSecretKey());
	}

	@Bean
	public UploadManager qiniuUploadManager() {
		com.qiniu.storage.Configuration configuration =
				new com.qiniu.storage.Configuration(Region.autoRegion());
		return new UploadManager(configuration);
	}
}