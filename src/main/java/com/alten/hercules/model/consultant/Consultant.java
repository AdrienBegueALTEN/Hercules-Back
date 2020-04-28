package com.alten.hercules.model.consultant;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.alten.hercules.model.diploma.Diploma;
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
	
	@Column(nullable = false)
	private int experience;
	
	@Column(nullable = true)
	private Date releaseDate = null;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Manager manager;
	
	@OneToMany
	private Set<Diploma> diplomas;
	
	@OneToMany
	private Set<Mission> missions;
	
	public Consultant() { super(); }
	
	public Consultant(String email, String firstname, String lastname, int experience, Manager manager, Set<Diploma> diplomas) {
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.experience = experience;
		this.manager = manager;
		this.diplomas = diplomas;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }

	public int getExperience() { return experience; }
	public void setExperience(int experience) { this.experience = experience; }
	
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