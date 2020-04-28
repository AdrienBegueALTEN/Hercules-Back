package com.alten.hercules.model.response;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import com.alten.hercules.model.user.AppUser;
import com.alten.hercules.security.jwt.JwtUtils;

public class JWTResponse {
	
	private String token;
	private Long id;
	private String firstname;
	private String lastname;
	private List<String> roles;
	
	public JWTResponse(Authentication authentication) {
		this.token = JwtUtils.generateJWT(authentication);
		AppUser user = (AppUser) authentication.getPrincipal();
		this.id = user.getId();
		this.firstname = user.getFirstname();
		this.lastname = user.getLastname();
		this.roles = user.getAuthorities().stream()
				.map(authority ->  authority.getAuthority())
				.collect(Collectors.toList());
	}

	public String getAccessToken() { return token; }
	public void setAccessToken(String accessToken) { this.token = accessToken; }
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }

	public List<String> getRoles() { return roles; }
	public void setRoles(List<String> roles) { this.roles = roles; }

}
