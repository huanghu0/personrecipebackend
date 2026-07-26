package com.personrecipe.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改个人资料。nickname / avatarUrl 均可选，至少传一个；
 * 只传其中一个时只更新对应字段。
 */
@Data
public class UpdateProfileRequest {

	@Size(max = 64, message = "昵称最多64字")
	private String nickname;

	@Size(max = 512, message = "头像地址过长")
	private String avatarUrl;
}
