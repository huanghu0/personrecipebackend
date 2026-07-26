package com.personrecipe.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 对应表 users。
 */
@Getter
@Setter
public class User {

	private Long id;

	private String openid;

	private String unionid;

	private String nickname;

	private String avatarUrl;

	private String sessionKey;

	private LocalDateTime lastLoginAt;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
