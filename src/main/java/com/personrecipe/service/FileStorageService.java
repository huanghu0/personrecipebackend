package com.personrecipe.service;

import com.personrecipe.common.BusinessException;
import com.personrecipe.config.QiniuProperties;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");
	private static final String OBJECT_PREFIX = "uploads";

	private final QiniuProperties qiniuProperties;
	private final Auth qiniuAuth;
	private final UploadManager qiniuUploadManager;

	/**
	 * 由后端接收图片并中转上传到七牛云，返回可直接访问的完整 URL。
	 */
	public String storeImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new BusinessException("请选择要上传的图片");
		}

		String extension = resolveExtension(file.getOriginalFilename(), file.getContentType());
		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new BusinessException("仅支持 jpg/png/webp/gif 图片");
		}

		String objectKey = buildObjectKey(extension);
		String uploadToken = qiniuAuth.uploadToken(qiniuProperties.getBucket(), objectKey);
		Response response = null;

		try (InputStream inputStream = file.getInputStream()) {
			response = qiniuUploadManager.put(
					inputStream,
					file.getSize(),
					objectKey,
					uploadToken,
					null,
					resolveMimeType(extension),
					false
			);

			if (!response.isOK()) {
				log.warn("七牛云上传失败: status={}", response.statusCode);
				throw new BusinessException(502, "图片上传到七牛云失败");
			}

			DefaultPutRet putRet = response.jsonToObject(DefaultPutRet.class);
			if (putRet == null || !StringUtils.hasText(putRet.key)) {
				throw new BusinessException(502, "七牛云上传结果异常");
			}

			return qiniuProperties.normalizedDomain() + "/" + putRet.key;
		} catch (QiniuException e) {
			log.warn("七牛云上传异常: status={}, error={}", e.code(), e.error());
			throw new BusinessException(502, "图片上传到七牛云失败");
		} catch (IOException e) {
			log.warn("读取上传图片失败", e);
			throw new BusinessException(500, "图片读取失败");
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	private String buildObjectKey(String extension) {
		String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		String filename = UUID.randomUUID().toString().replace("-", "") + "." + extension;
		return OBJECT_PREFIX + "/" + datePath + "/" + filename;
	}

	private String resolveMimeType(String extension) {
		return switch (extension) {
			case "jpg", "jpeg" -> "image/jpeg";
			case "png" -> "image/png";
			case "webp" -> "image/webp";
			case "gif" -> "image/gif";
			default -> "application/octet-stream";
		};
	}

	private String resolveExtension(String originalFilename, String contentType) {
		String extension = "";
		if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
			extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1)
					.toLowerCase(Locale.ROOT);
		}
		if (!StringUtils.hasText(extension) && StringUtils.hasText(contentType)) {
			extension = switch (contentType.toLowerCase(Locale.ROOT)) {
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