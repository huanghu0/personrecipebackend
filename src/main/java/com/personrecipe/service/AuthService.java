package com.personrecipe.service;

import com.personrecipe.common.BusinessException;
import com.personrecipe.dto.LoginResponse;
import com.personrecipe.dto.UserInfoVO;
import com.personrecipe.dto.WeChatSessionResponse;
import com.personrecipe.dto.WxLoginRequest;
import com.personrecipe.entity.User;
import com.personrecipe.mapper.UserMapper;
import com.personrecipe.security.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final WeChatService weChatService;
	private final UserMapper userMapper;
	private final TokenBlacklistService tokenBlacklistService;
	private final JwtUtil jwtUtil;

	@Transactional
	public LoginResponse wxLogin(WxLoginRequest request) {
		WeChatSessionResponse session = weChatService.code2Session(request.getCode());

		User user = userMapper.findByOpenid(session.getOpenid());
		boolean isNewUser = user == null;
		if (isNewUser) {
			user = new User();
			user.setOpenid(session.getOpenid());
		}

		user.setSessionKey(session.getSessionKey());
		if (StringUtils.hasText(session.getUnionid())) {
			user.setUnionid(session.getUnionid());
		}
		if (StringUtils.hasText(request.getNickname())) {
			user.setNickname(request.getNickname());
		}
		if (StringUtils.hasText(request.getAvatarUrl())) {
			user.setAvatarUrl(request.getAvatarUrl());
		}
		user.setLastLoginAt(LocalDateTime.now());

		if (isNewUser) {
			userMapper.insert(user);
		} else {
			userMapper.update(user);
		}

		String token = jwtUtil.generateToken(user.getId(), user.getOpenid());
		return LoginResponse.builder()
				.token(token)
				.expiresIn(jwtUtil.getExpireSeconds())
				.user(UserInfoVO.builder()
						.id(user.getId())
						.nickname(user.getNickname())
						.avatarUrl(user.getAvatarUrl())
						.build())
				.build();
	}

	/**
	 * 退出登录：把当前 Token 加入 Redis 黑名单，并清空 session_key。
	 * 重复登出视为成功（幂等）。
	 */
	@Transactional
	public void logout(String token) {
		Claims claims = jwtUtil.parseClaims(token);
		String jti = claims.getId();
		if (!StringUtils.hasText(jti)) {
			throw new BusinessException(401, "无效的登录凭证");
		}

		Long userId = Long.valueOf(claims.getSubject());
		Duration ttl = Duration.between(Instant.now(), claims.getExpiration().toInstant());
		tokenBlacklistService.add(jti, ttl);

		userMapper.clearSessionKey(userId);
	}
}
