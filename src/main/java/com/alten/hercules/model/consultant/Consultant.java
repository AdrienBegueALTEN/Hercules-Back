package com.alten.hercules.model.consultant;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.alten.hercules.StrUtils;
import com.alten.hercules.model.diploma.Diploma;
import com.alten.hercules.model.exception.InvalidRessourceFormatException;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.user.Manager;
import com.alten.hercules.model.user.response.ManagerResponse;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Consultant {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String firstname;
	
	@Column(nullable = false)
	private String lastname;
	
	@Column(nullable = false, columnDefinition = "integer default 0")
	private int experience;
	
	@Column(nullable = true)
	private Date releaseDate = null;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Manager manager;
	
	@ManyToMany
	private Set<Diploma> diplomas;
	
	@OneToMany(mappedBy="consultant")
	private Set<Mission> missions;
	
	public Consultant() { super(); }
	
	public Consultant(String email, String firstname, String lastname, Manager manager) {
		setEmail(email);
		setFirstname(firstname);
		setLastname(lastname);
		setManager(manager);
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getEmail() { return email; }
	public void setEmail(String email) throws InvalidValueException, InvalidRessourceFormatException { 
		if (email == null)
			throw new InvalidValueException("consultant.mail");
		if (!Pattern.matches(StrUtils.EMAIL_PATTERN, email))
			throw new InvalidRessourceFormatException("consultant.email");
		this.email = email;
	}

	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) throws InvalidValueException, InvalidRessourceFormatException {
		if (firstname == null)
			throw new InvalidValueException("consultant.firstname");
		if (!Pattern.matches(StrUtils.NAME_PATTERN, firstname))
			throw new InvalidRessourceFormatException("consultant.firstname");
		this.firstname = StrUtils.formaliseFirstname(firstname);
	}

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) throws InvalidValueException, InvalidRessourceFormatException {
		if (lastname == null)
			throw new InvalidValueException("consultant.lastname");
		if (!Pattern.matches(StrUtils.NAME_PATTERN, lastname))
			throw new InvalidRessourceFormatException("consultant.lastname");
		this.lastname = lastname;
	}

	public int getExperience() { return experience; }
	public void setExperience(Integer experience) throws InvalidValueException { 
		if (experience == null || experience < 0)
			throw new InvalidValueException("consultant.experience");
		this.experience = experience;
	}
	
	public String getReadableReleaseDate() { 
		return (releaseDate == null) ? null :
			new SimpleDateFormat("dd/MM/yyyy").format(releaseDate);
	}
	public Date getReleaseDate() { return releaseDate; }
	public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

	public Manager getManager() { return manager; }
	public void setManager(Manager manager) { this.manager = manager; }

	public Set<Diploma> getDiplomas() { return diplomas; }
	public void setDiploma(Set<Diploma> diplomas) { this.diplomas = diplomas; }
	
	public Set<Mission> getMissions() { return missions; }
	public void setMissions(Set<Mission> missions) { this.missions = missions; }

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Consultant consultant = (Consultant) o;
		return Objects.equals(id, consultant.id);
	}
	
	@JsonGetter("manager")
    private ManagerResponse getManagerId() {
		
        if (this.manager != null)
        	return new ManagerResponse(this.manager);
        return null;
    }
}