package com.personrecipe.service;

import com.personrecipe.common.BusinessException;
import com.personrecipe.config.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

	private final FileStorageProperties fileStorageProperties;

	/**
	 * 保存图片到本地，返回可访问的相对 URL（如 /uploads/2026/07/26/xxx.jpg）。
	 */
	public String storeImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new BusinessException("请选择要上传的图片");
		}

		String originalFilename = file.getOriginalFilename();
		String extension = resolveExtension(originalFilename, file.getContentType());
		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new BusinessException("仅支持 jpg/png/webp/gif 图片");
		}

		String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		String filename = UUID.randomUUID().toString().replace("-", "") + "." + extension;

		Path targetDir = Paths.get(fileStorageProperties.getUploadDir(), datePath.split("/"))
				.toAbsolutePath()
				.normalize();
		try {
			Files.createDirectories(targetDir);
			Path targetFile = targetDir.resolve(filename);
			Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new BusinessException(500, "图片保存失败");
		}

		String accessPath = fileStorageProperties.getAccessPath();
		if (accessPath.endsWith("/")) {
			accessPath = accessPath.substring(0, accessPath.length() - 1);
		}
		return accessPath + "/" + datePath + "/" + filename;
	}

	private String resolveExtension(String originalFilename, String contentType) {
		String extension = "";
		if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
			extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
		}
		if (!StringUtils.hasText(extension) && StringUtils.hasText(contentType)) {
			extension = switch (contentType.toLowerCase()) {
				case "image/jpeg" -> "jpg";
				case "image/png" -> "png";
				case "image/webp" -> "webp";
				case "image/gif" -> "gif";
				default -> "";
			};
		}
		return extension;
	}
}
