package com.alten.hercules.model.consultant;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.alten.hercules.model.diploma.Diploma;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.user.Manager;
import com.alten.hercules.utils.StrUtils;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Class model for a consultant.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Consultant {
	
	/**
	 * ID of the consultant
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * Email of the consultant
	 */
	@Column(nullable = false)
	private String email;
	
	/**
	 * First name of the consultant
	 */
	@Column(nullable = false)
	private String firstname;
	
	/**
	 * Last name of the consultant
	 */
	@Column(nullable = false)
	private String lastname;
	
	/**
	 * Years of experience of the consultant
	 */
	@Column(nullable = false, columnDefinition = "integer default 0")
	private int experience;
	
	/**
	 * Date of release of the consultant
	 */
	@Column(nullable = true)
	private LocalDate releaseDate = null;
	
	/**
	 * Manager of the consultant
	 */
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Manager manager;
	
	/**
	 * Set of diplomas of the consultant
	 */
	@OneToMany(fetch = FetchType.LAZY)
	private Set<Diploma> diplomas;
	
	/**
	 * Set of missions of the consultant
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy="consultant")
	private Set<Mission> missions;
	
	/**
	 * Empty constructor
	 */
	public Consultant() { super(); }
	
	/**
	 * Constructor
	 */
	public Consultant(String email, String firstname, String lastname, Manager manager) throws InvalidValueException {
		setEmail(email);
		setFirstname(firstname);
		setLastname(lastname);
		setManager(manager);
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getEmail() { return email; }
	public void setEmail(String email) throws InvalidValueException { 
		if (!Pattern.matches(StrUtils.EMAIL_PATTERN, email))
			throw new InvalidValueException();
		this.email = email;
	}

	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) throws InvalidValueException {
		if (!Pattern.matches(StrUtils.NAME_PATTERN, firstname))
			throw new InvalidValueException();
		this.firstname = StrUtils.formaliseFirstname(firstname);
	}

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) throws InvalidValueException {
		if (!Pattern.matches(StrUtils.NAME_PATTERN, lastname))
			throw new InvalidValueException();
		this.lastname = lastname;
	}

	public int getExperience() { return experience; }
	public void setExperience(Integer experience) throws InvalidValueException { 
		if (experience < 0) throw new InvalidValueException();
		this.experience = experience;
	}
	
	public LocalDate getReleaseDate() { return releaseDate; }
	public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

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
	
	/**
	 * Function that generates a response with the manager of this consultant details
	 * @return A map with the details of the manager
	 */
	@JsonGetter("manager")
    public Map<String, Object> getManagerResponse() {
		Map<String, Object> manager = new HashMap<>();
		manager.put("id", this.manager.getId());
		manager.put("email", this.manager.getEmail());
		manager.put("firstname", this.manager.getFirstname());
		manager.put("lastname", this.manager.getLastname());
		if (this.manager.getReleaseDate() != null)
			manager.put("releaseDate", this.manager.getReleaseDate());
        return manager;
    }

	public void addDiploma(Diploma diploma) {
		diplomas.add(diploma);
	}
	
	public void removeDiploma(Diploma diploma) {
		diplomas.remove(diploma);
	}
}