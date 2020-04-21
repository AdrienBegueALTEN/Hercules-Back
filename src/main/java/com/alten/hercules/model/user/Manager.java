package com.alten.hercules.model.user;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.alten.hercules.model.consultant.Consultant;

@Entity
public class Manager extends AppUser {
	@Transient
	private static final long serialVersionUID = 1L;
	
	private boolean isAdmin = false;
	
    @OneToMany
    private Set<Consultant> consultants = new HashSet<>();
	
	public Manager() { super(); }
	
	public Manager(String email, String password, String firstname, String lastname) {
		super(email, password, firstname, lastname);
	}
	
	public Manager(String email, String password, String firstname, String lastname, boolean isAdmin) {
		super(email, password, firstname, lastname);
		this.setAdmin(isAdmin);
	}

	@Override
	public ERole getRole() {
		return isAdmin ? ERole.ADMIN : ERole.MANAGER;
	}

	public boolean isAdmin() { return isAdmin; }
	public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
	
	public Set<Consultant> getConsultants() { return consultants; }
	public void setConsultants(Set<Consultant> consultants) { this.consultants = consultants; }

}
