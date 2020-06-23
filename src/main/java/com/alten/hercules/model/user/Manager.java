package com.alten.hercules.model.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.exception.InvalidValueException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Class model for a manager.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Manager extends AppUser {
	
	private boolean isAdmin = false;
	
    @OneToMany(mappedBy="manager")
    private Set<Consultant> consultants = new HashSet<>();
	
	public Manager() { super(); }
	
	public Manager(String email, String password, String firstname, String lastname) throws InvalidValueException {
		super(email, password, firstname, lastname);
	}
	
	public Manager(String email, String password, String firstname, String lastname, boolean isAdmin) throws InvalidValueException {
		super(email, password, firstname, lastname);
		setAdmin(isAdmin);
	}
	
	public Manager(String email, String firstname, String lastname, boolean isAdmin) throws InvalidValueException {
		super(email, null, firstname, lastname);
		setAdmin(isAdmin);
	}
	
	
	public boolean isAdmin() { return isAdmin; }
	public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
	
	public Set<Consultant> getConsultants() { return consultants; }
	public void setConsultants(Set<Consultant> consultants) { this.consultants = consultants; }

	@JsonIgnore
	@Override
	public Collection<SimpleGrantedAuthority> getAuthorities() {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(EAuthorities.MANAGER.name()));
		if (isAdmin) authorities.add(new SimpleGrantedAuthority(EAuthorities.ADMIN.name()));
		return authorities;
	}

}
