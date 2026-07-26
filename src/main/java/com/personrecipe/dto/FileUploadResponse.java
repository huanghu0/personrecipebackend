package com.personrecipe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponse {

	/**
	 * 可访问的相对路径，如 /uploads/2026/07/26/xxx.jpg。
	 */
	private String url;
}
