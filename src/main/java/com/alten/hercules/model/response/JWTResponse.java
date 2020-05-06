package com.alten.hercules.model.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.alten.hercules.model.user.AppUser;
import com.alten.hercules.security.jwt.JwtUtils;

public class JWTResponse {
	
	private String accessToken;
	private Map<String, Object> user;

	public JWTResponse(AppUser user) {
		accessToken = JwtUtils.generateJwt(user);
		this.user = new HashMap<String, Object>();
		this.user.put("id", user.getId());
		this.user.put("firstname", user.getFirstname());
		this.user.put("lastname", user.getLastname());
		this.user.put("roles", user.getAuthorities().stream()
				.map(authority -> authority.getAuthority())
				.collect(Collectors.toList()));
	}

	public String getAccessToken() { return accessToken; }
	public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
	
	public Map<String, Object> getUser() { return user; }
	public void setUser(Map<String, Object> user) { this.user = user; }
	
}
