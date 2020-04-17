package com.alten.hercules.model.response;

import org.springframework.security.core.Authentication;

import com.alten.hercules.security.jwt.JwtUtils;

public class JWTResponse {
	
	private String accessToken;
	
	public JWTResponse(Authentication authentication) {
		this.accessToken = JwtUtils.generateJWT(authentication);
	}

	public String getAccessToken() { return accessToken; }
	public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

}
