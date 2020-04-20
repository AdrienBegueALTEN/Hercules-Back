package com.alten.hercules.model.request.user.manager;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.alten.hercules.consts.UserConst;
import com.fasterxml.jackson.annotation.JsonFormat;

public class UpdateManagerRequest {
	
	@NotNull
	private Long id;
	
	@Pattern(regexp = UserConst.EMAIL_PATTERN, message = UserConst.EMAIL_PATTERN_MSG)
	private String email;
	
	private String password;
	
	private String firstname;
	
	private String lastname;
	
	@JsonFormat(pattern="dd/MM/yyy")
	private LocalDate releaseDate;
	
	public UpdateManagerRequest(Long id, String email, String password, String firstname, String lastname, LocalDate releaseDate, boolean isAdmin) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.releaseDate = releaseDate;
		this.isAdmin = isAdmin;
	}
	
	private boolean isAdmin;
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public void setEmail(String email) { this.email = email; }
	public String getEmail() { return this.email; }
	
	public void setPassword(String password) { this.email = password; }
	public String getPassword() { return password; }
	
	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }
	
	public LocalDate getReleaseDate() { return releaseDate; }
	public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
	
	public boolean isAdmin() { return isAdmin; }
	public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
	
}
