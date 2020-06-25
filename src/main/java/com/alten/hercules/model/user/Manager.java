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
	
	/**
	 * Boolean that indicates if the manager has the administration rights
	 */
	private boolean isAdmin = false;
	
	/**
	 * Set of consultants of the manager
	 */
    @OneToMany(mappedBy="manager")
    private Set<Consultant> consultants = new HashSet<>();
	
    /**
     * Empty constructor
     */
	public Manager() { super(); }
	
	/**
	 * Simplified constructor
	 */
	public Manager(String email, String firstname, String lastname) throws InvalidValueException {
		super(email, firstname, lastname);
		setAdmin(false);
	}
	
	/**
	 * Complete constructor
	 */
	public Manager(String email, String firstname, String lastname, boolean isAdmin) throws InvalidValueException {
		super(email, firstname, lastname);
		setAdmin(isAdmin);
	}
	
	public boolean isAdmin() { return isAdmin; }
	public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
	
	public Set<Consultant> getConsultants() { return consultants; }
	public void setConsultants(Set<Consultant> consultants) { this.consultants = consultants; }
	
	/**
	 * Function that returns the authorizations of the manager
	 */
	@JsonIgnore
	@Override
	public Collection<SimpleGrantedAuthority> getAuthorities() {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(EAuthorities.MANAGER.name()));
		if (isAdmin) authorities.add(new SimpleGrantedAuthority(EAuthorities.ADMIN.name()));
		return authorities;
	}

}
