package com.personrecipe.config;

import com.personrecipe.security.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	private final AuthInterceptor authInterceptor;
	private final FileStorageProperties fileStorageProperties;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
				.addPathPatterns("/api/**")
				.excludePathPatterns(
						"/api/auth/wx-login"
				);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String location = Paths.get(fileStorageProperties.getUploadDir())
				.toAbsolutePath()
				.normalize()
				.toUri()
				.toString();
		String pattern = fileStorageProperties.getAccessPath();
		if (!pattern.endsWith("/")) {
			pattern = pattern + "/";
		}
		registry.addResourceHandler(pattern + "**")
				.addResourceLocations(location.endsWith("/") ? location : location + "/");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOriginPatterns("*")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(3600);
		registry.addMapping(fileStorageProperties.getAccessPath() + "/**")
				.allowedOriginPatterns("*")
				.allowedMethods("GET", "OPTIONS")
				.allowedHeaders("*")
				.maxAge(3600);
	}
}
