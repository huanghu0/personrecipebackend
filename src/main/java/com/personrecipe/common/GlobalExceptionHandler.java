package com.personrecipe.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<Void> handleBusiness(BusinessException e) {
		return ApiResponse.fail(e.getCode(), e.getMessage());
	}

	@ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<Void> handleValidation(Exception e) {
		String message = "参数错误";
		if (e instanceof MethodArgumentNotValidException manv && manv.getBindingResult().getFieldError() != null) {
			message = manv.getBindingResult().getFieldError().getDefaultMessage();
		} else if (e instanceof BindException be && be.getBindingResult().getFieldError() != null) {
			message = be.getBindingResult().getFieldError().getDefaultMessage();
		}
		return ApiResponse.fail(400, message);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<Void> handleMaxUpload(MaxUploadSizeExceededException e) {
		return ApiResponse.fail(400, "图片过大，单张不超过 10MB");
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<Void> handleOther(Exception e, HttpServletRequest request) {
		log.error("Unhandled error on {}", request.getRequestURI(), e);
		return ApiResponse.fail(500, "服务器内部错误");
	}
}
