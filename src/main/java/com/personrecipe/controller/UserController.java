package com.personrecipe.controller;

import com.personrecipe.common.ApiResponse;
import com.personrecipe.dto.UpdateNicknameRequest;
import com.personrecipe.dto.UpdateProfileRequest;
import com.personrecipe.dto.UserInfoVO;
import com.personrecipe.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	/**
	 * 获取当前登录用户资料。
	 */
	@GetMapping("/profile")
	public ApiResponse<UserInfoVO> profile() {
		return ApiResponse.ok(userService.getProfile());
	}

	/**
	 * 修改资料：昵称、头像可单独传，也可一起传。
	 * 头像字段为已上传得到的 URL（可先调 /api/files/upload 或本模块头像上传接口）。
	 */
	@PutMapping("/profile")
	public ApiResponse<UserInfoVO> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
		return ApiResponse.ok(userService.updateProfile(request));
	}

	/**
	 * 单独修改昵称。
	 */
	@PutMapping("/nickname")
	public ApiResponse<UserInfoVO> updateNickname(@Valid @RequestBody UpdateNicknameRequest request) {
		return ApiResponse.ok(userService.updateNickname(request.getNickname()));
	}

	/**
	 * 单独上传并更新头像（multipart，字段名 file）。
	 */
	@PostMapping("/avatar")
	public ApiResponse<UserInfoVO> uploadAvatar(@RequestParam("file") MultipartFile file) {
		return ApiResponse.ok(userService.uploadAvatar(file));
	}
}
