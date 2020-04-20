package com.alten.hercules.model.request.consultant;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.alten.hercules.consts.UserConst;
import com.alten.hercules.model.diploma.Diploma;

public class AddConsultantRequest {
	
	@NotNull
	@Pattern(regexp = UserConst.EMAIL_PATTERN, message = UserConst.EMAIL_PATTERN_MSG)
	private String email;

	@NotNull
	private String firstname;
	
	@NotNull
	private String lastname;
	
	@Min(value = 0)
	private int experience;
	
	@NotNull
	private Long manager;
	
	private Diploma[] diplomas;
	
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }
	
	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }

	public int getExperience() { return experience; }
	public void setExperience(int experience) { this.experience = experience; }

	public Long getManager() { return manager; }
	public void setManager(Long idManager) { this.manager = idManager; }

	public Diploma[] getDiplomas() { return diplomas; }
	public void setDiplomas(Diploma[] diplomas) { this.diplomas = diplomas; }

}
