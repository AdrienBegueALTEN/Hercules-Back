package com.alten.hercules.model.user;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class RecruitementOfficer extends AppUser {
	@Transient
	private static final long serialVersionUID = 1L;

	public RecruitementOfficer() { super(); }
	
	public RecruitementOfficer(String email, String password, String firstname, String lastname) {
		super(email, password, firstname, lastname);
	}
	
	public RecruitementOfficer(String email, String password, String firstname, String lastname, LocalDate releaseDate) {
		super(email, password, firstname, lastname, releaseDate);
	}

	@Override
	public ERole getRole() {
		return ERole.RECRUITEMENT_OFFICER;
	}

}
