package com.personrecipe.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeChatSessionResponse {

	private String openid;

	@JsonProperty("session_key")
	private String sessionKey;

	private String unionid;

	private Integer errcode;

	private String errmsg;
}
