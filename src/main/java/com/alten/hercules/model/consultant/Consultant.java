package com.alten.hercules.model.consultant;


import java.time.LocalDate;
import java.util.List;
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
import com.alten.hercules.model.user.Manager;

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
	private LocalDate releaseDate = null;

	@ManyToOne(fetch = FetchType.LAZY)
	private Manager manager;
	
	@OneToMany
	private Set<Diploma> diplomas;
	
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
	
	public LocalDate getReleaseDate() { return releaseDate; }
	public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

	public Manager getManager() { return manager; }
	public void setManager(Manager manager) { this.manager = manager; }

	public Set<Diploma> getDiplomas() { return diplomas; }
	public void setDiploma(Set<Diploma> diplomas) { this.diplomas = diplomas; }

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Consultant consultant = (Consultant) o;
		return Objects.equals(id, consultant.id);
	}
}