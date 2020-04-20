package com.alten.hercules.model.request.user.recruitementOfficer;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.alten.hercules.consts.UserConst;

public class UpdateRecruitementOfficerRequest {
	
	@NotNull
	private Long id;

	@Pattern(regexp = UserConst.EMAIL_PATTERN, message = UserConst.EMAIL_PATTERN_MSG)
	private String email;
	
	private String password;
	
	private String firstname;
	
	private String lastname;
	
	private LocalDate releaseDate;
	
	public UpdateRecruitementOfficerRequest(Long id, String email, String password, String firstname, String lastname, LocalDate releaseDate) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.releaseDate = releaseDate;
	}
	
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
	
	public LocalDate getReleaseDate() { return releaseDate; }
	public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate;}

}
