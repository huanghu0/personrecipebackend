package com.personrecipe.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

	/**
	 * 业务成功码。
	 */
	public static final int SUCCESS_CODE = 200;

	private int code;
	private String message;
	private T data;

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(SUCCESS_CODE, "ok", data);
	}

	public static <T> ApiResponse<T> fail(int code, String message) {
		return new ApiResponse<>(code, message, null);
	}
}
