package com.alten.hercules.model.user;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Entity;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.alten.hercules.model.exception.InvalidValueException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Class model for a recruitment officer.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RecruitmentOfficer extends AppUser {

	public RecruitmentOfficer() { super(); }
	
	public RecruitmentOfficer(String email, String firstname, String lastname) throws InvalidValueException {
		super(email, firstname, lastname);
	}

	@JsonIgnore
	@Override
	public Collection<SimpleGrantedAuthority> getAuthorities() {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(EAuthorities.RECRUITMENT_OFFICER.name()));
		return authorities;
	}

}
