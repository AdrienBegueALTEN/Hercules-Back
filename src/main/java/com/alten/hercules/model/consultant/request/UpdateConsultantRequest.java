package com.alten.hercules.model.consultant.request;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.type.descriptor.sql.DateTypeDescriptor;

import com.alten.hercules.consts.UserConst;

public class UpdateConsultantRequest {
	
	@NotNull
	private Long id;
	
	@Pattern(regexp = UserConst.EMAIL_PATTERN, message = UserConst.EMAIL_PATTERN_MSG)
	private String email;

	private String firstname;
	
	private String lastname;
	
	@Min(value = 0)
	private Integer experience;
	
	private Long manager;
	
	private LocalDate releaseDate;
	
	
	
	
	public UpdateConsultantRequest() {
		super();
	}
	public UpdateConsultantRequest(@NotNull Long id,
			@Pattern(regexp = "[a-zA-Z]+\\.[a-zA-Z]+[2-9]?@alten\\.com", message = "Format : prenom.nom@alten.com") String email,
			String firstname, String lastname, @Min(0) Integer experience, Long manager, LocalDate releaseDate) {
		super();
		this.id = id;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.experience = experience;
		this.manager = manager;
		this.releaseDate = releaseDate;
	}
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	
	public LocalDate getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }
	
	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }

	public Integer getExperience() { return experience; }
	public void setExperience(Integer experience) { this.experience = experience; }

	public Long getManager() { return manager; }
	public void setManager(Long idManager) { this.manager = idManager; }

}
