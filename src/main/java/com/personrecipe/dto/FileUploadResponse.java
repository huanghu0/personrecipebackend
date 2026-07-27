package com.personrecipe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponse {

	/**
	 * 七牛云上的完整访问地址。
	 */
	private String url;
}
