package com.alten.hercules.model.response;

import java.util.List;

public class JwtResponse {
	
	private String token;
	private final String type = "JWT";
	private Long id;
	private String email;
	private String firstname;
	private String lastname;
	private List<String> roles;
	
	public JwtResponse(String token, Long id, String email, String firstname, String lastname, List<String> roles) {
		this.token = token;
		this.id = id;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.roles = roles;
	}
	
	public String getToken() { return token; }
	public void setToken(String token) { this.token = token; }
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	
	public List<String> getRoles() { return roles; }
	public void setRoles(List<String> roles) { this.roles = roles; }
	
	public String getType() { return type; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }

	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

}
