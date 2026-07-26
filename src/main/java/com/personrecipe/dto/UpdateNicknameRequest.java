package com.personrecipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateNicknameRequest {

	@NotBlank(message = "昵称不能为空")
	@Size(max = 64, message = "昵称最多64字")
	private String nickname;
}
