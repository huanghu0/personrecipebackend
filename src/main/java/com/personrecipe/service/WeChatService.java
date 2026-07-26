package com.personrecipe.service;

import com.personrecipe.common.BusinessException;
import com.personrecipe.config.WeChatProperties;
import com.personrecipe.dto.WeChatSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeChatService {

	private static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

	private final RestClient restClient = RestClient.create();

	private final JsonMapper jsonMapper = JsonMapper.builder().build();

	private final WeChatProperties weChatProperties;

	public WeChatSessionResponse code2Session(String code) {
		if (weChatProperties.isMockEnabled()) {
			log.warn("微信 mock 模式已开启，code 将直接作为 openid 使用");
			WeChatSessionResponse mock = new WeChatSessionResponse();
			mock.setOpenid(code);
			mock.setSessionKey("mock-session-key");
			return mock;
		}

		if (!StringUtils.hasText(weChatProperties.getAppId())
				|| !StringUtils.hasText(weChatProperties.getAppSecret())
				|| "your-app-id".equals(weChatProperties.getAppId())) {
			throw new BusinessException(500, "未配置微信小程序 appId/appSecret");
		}

		String url = UriComponentsBuilder.fromUriString(CODE2SESSION_URL)
				.queryParam("appid", weChatProperties.getAppId())
				.queryParam("secret", weChatProperties.getAppSecret())
				.queryParam("js_code", code)
				.queryParam("grant_type", "authorization_code")
				.toUriString();

		// 微信 jscode2session 返回的是 JSON，但 Content-Type 是 text/plain，
		// 直接用 body(Class) 会报 no suitable HttpMessageConverter，故先取字符串再手动解析。
		String rawBody = restClient
				.get()
				.uri(url)
				.retrieve()
				.body(String.class);

		if (!StringUtils.hasText(rawBody)) {
			throw new BusinessException(502, "微信登录服务无响应");
		}

		WeChatSessionResponse response;
		try {
			response = jsonMapper.readValue(rawBody, WeChatSessionResponse.class);
		} catch (Exception e) {
			log.warn("解析微信 code2session 响应失败: {}", rawBody, e);
			throw new BusinessException(502, "微信登录服务返回异常");
		}
		if (response == null) {
			throw new BusinessException(502, "微信登录服务无响应");
		}
		if (response.getErrcode() != null && response.getErrcode() != 0) {
			log.warn("微信 code2session 失败: errcode={}, errmsg={}", response.getErrcode(), response.getErrmsg());
			throw new BusinessException(401, "微信登录失败: " + response.getErrmsg());
		}
		if (!StringUtils.hasText(response.getOpenid())) {
			throw new BusinessException(401, "微信登录失败：未获取到 openid");
		}
		return response;
	}
}
