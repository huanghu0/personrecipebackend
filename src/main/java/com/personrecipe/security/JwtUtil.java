package com.personrecipe.security;

import com.personrecipe.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

	private final JwtProperties jwtProperties;
	private final SecretKey secretKey;

	public JwtUtil(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < 32) {
			throw new IllegalStateException("jwt.secret 长度至少 32 字节");
		}
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(Long userId, String openid) {
		Instant now = Instant.now();
		Instant expireAt = now.plus(jwtProperties.getExpireDays(), ChronoUnit.DAYS);
		return Jwts.builder()
				.id(UUID.randomUUID().toString().replace("-", ""))
				.subject(String.valueOf(userId))
				.claim("openid", openid)
				.issuedAt(Date.from(now))
				.expiration(Date.from(expireAt))
				.signWith(secretKey)
				.compact();
	}

	public long getExpireSeconds() {
		return jwtProperties.getExpireDays() * 24L * 60L * 60L;
	}

	public Long parseUserId(String token) {
		return Long.valueOf(parseClaims(token).getSubject());
	}

	public Claims parseClaims(String token) {
		try {
			return Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (ExpiredJwtException e) {
			throw new com.personrecipe.common.BusinessException(401, "登录已过期，请重新登录");
		} catch (JwtException | IllegalArgumentException e) {
			throw new com.personrecipe.common.BusinessException(401, "无效的登录凭证");
		}
	}
}
