package com.alten.hercules.controller.user.http.request.recruitementOfficer;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.alten.hercules.consts.AppConst;

public class UpdateRecruitementOfficerRequest {
	
	@NotNull
	private Long id;

	@Pattern(regexp = AppConst.EMAIL_PATTERN)
	private String email;
	
	private String password;
	
	private String firstname;
	
	private String lastname;
	
	private Date releaseDate;
	
	public UpdateRecruitementOfficerRequest(Long id, String email, String password, String firstname, String lastname, Date releaseDate) {
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
	
	public Date getReleaseDate() { return releaseDate; }
	public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate;}

}
