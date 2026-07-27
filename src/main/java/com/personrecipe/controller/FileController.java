package com.personrecipe.controller;

import com.personrecipe.common.ApiResponse;
import com.personrecipe.dto.FileUploadResponse;
import com.personrecipe.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

	private final FileStorageService fileStorageService;

	/**
	 * 由后端接收图片并上传到七牛云，返回可访问 URL。
	 * 表单字段名：file
	 */
	@PostMapping("/upload")
	public ApiResponse<FileUploadResponse> upload(@RequestParam("file") MultipartFile file) {
		String url = fileStorageService.storeImage(file);
		return ApiResponse.ok(FileUploadResponse.builder().url(url).build());
	}
}
