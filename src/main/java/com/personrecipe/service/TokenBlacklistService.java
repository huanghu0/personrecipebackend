package com.personrecipe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * JWT 黑名单：登出后把 Token 的 jti 存入 Redis，
 * TTL 设为 Token 剩余有效期，过期后自动清理。
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

	private static final String KEY_PREFIX = "jwt:blacklist:";

	private final StringRedisTemplate redisTemplate;

	public void add(String jti, Duration ttl) {
		if (ttl.isNegative() || ttl.isZero()) {
			return;
		}
		redisTemplate.opsForValue().set(KEY_PREFIX + jti, "1", ttl);
	}

	public boolean contains(String jti) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + jti));
	}
}
