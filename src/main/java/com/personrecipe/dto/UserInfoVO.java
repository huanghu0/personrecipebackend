package com.personrecipe.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserInfoVO {

	private Long id;
	private String nickname;
	private String avatarUrl;
	/** 注册时间 */
	private LocalDateTime createdAt;
}
