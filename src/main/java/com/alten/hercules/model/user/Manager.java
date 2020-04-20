package com.alten.hercules.model.user;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class Manager extends AppUser {
	@Transient
	private static final long serialVersionUID = 1L;
	
	private boolean isAdmin;
	
	public Manager() { super(); }
	
	public Manager(String email, String password, String firstname, String lastname) {
		super(email, password, firstname, lastname);
		this.setAdmin(false);
	}
	
	public Manager(String email, String password, String firstname, String lastname, boolean isAdmin) {
		super(email, password, firstname, lastname);
		this.setAdmin(isAdmin);
	}
	
	public Manager(String email, String password, String firstname, String lastname, boolean isAdmin, LocalDate releaseDate) {
		super(email, password, firstname, lastname, releaseDate);
		this.setAdmin(isAdmin);
	}

	@Override
	public ERole getRole() {
		return isAdmin ? ERole.ADMIN : ERole.MANAGER;
	}

	public boolean isAdmin() { return isAdmin; }
	public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }

}
