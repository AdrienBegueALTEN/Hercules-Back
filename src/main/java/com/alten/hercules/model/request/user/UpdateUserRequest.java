package com.alten.hercules.model.request.user;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.alten.hercules.consts.UserConst;

public class UpdateUserRequest {
	
	@NotNull
	private Long id;

	@Pattern(regexp = UserConst.EMAIL_PATTERN, message = UserConst.EMAIL_PATTERN_MSG)
	private String email;
	
	private String password;
	
	private String firstname;
	
	private String lastname;
	
	@Pattern(regexp = "ADMIN|MANAGER", message = "Le rôle doit être soit 'ADMIN', soit 'MANAGER'")
	private String role;
	
	private LocalDate releaseDate;
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getEmail() {return email; }
	public void setEmail(String email) {this.email = email; }

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }
	
	public String getRole() { return role; }
	public void setRole(String role) { this.role = role; }

	public LocalDate getReleaseDate() { return releaseDate; }
	public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate;}

}
