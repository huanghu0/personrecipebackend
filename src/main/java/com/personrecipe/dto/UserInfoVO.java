package com.personrecipe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoVO {

	private Long id;
	private String nickname;
	private String avatarUrl;
}
