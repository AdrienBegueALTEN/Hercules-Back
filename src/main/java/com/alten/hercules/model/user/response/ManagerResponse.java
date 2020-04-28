package com.alten.hercules.model.user.response;

import java.time.LocalDate;

import com.alten.hercules.model.user.Manager;

public class ManagerResponse {
	private Long id;
	private String email;
	private String lastname;
	private String firstname;
	private LocalDate releaseDate;
	
	public ManagerResponse() {
		super();
	}

	
	
	public ManagerResponse(Long id, String email, String lastname, String firstname, LocalDate releaseDate) {
		super();
		this.id = id;
		this.email = email;
		this.lastname = lastname;
		this.firstname = firstname;
		this.releaseDate = releaseDate;
	}



	public ManagerResponse(Manager m) {
		this(m.getId(), m.getEmail(), m.getLastname(), m.getFirstname(), m.getReleaseDate());
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getLastname() {
		return lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}
	
	
	
	
}
