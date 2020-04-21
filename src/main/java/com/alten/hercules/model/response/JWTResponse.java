package com.alten.hercules.model.response;

import org.springframework.security.core.Authentication;

import com.alten.hercules.model.user.AppUser;
import com.alten.hercules.security.jwt.JwtUtils;

public class JWTResponse {
	
	private String token;
	private Long id;
	private String firstname;
	private String lastname;
	private String role;
	
	public JWTResponse(Authentication authentication) {
		this.token = JwtUtils.generateJWT(authentication);
		AppUser user = (AppUser) authentication.getPrincipal();
		this.id = user.getId();
		this.firstname = user.getFirstname();
		this.lastname = user.getLastname();
		this.role = user.getRole().name();
	}

	public String getAccessToken() { return token; }
	public void setAccessToken(String accessToken) { this.token = accessToken; }
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }

	public String getRole() { return role; }
	public void setRole(String role) { this.role = role; }

}
