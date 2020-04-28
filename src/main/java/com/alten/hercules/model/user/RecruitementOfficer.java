package com.alten.hercules.model.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
public class RecruitementOfficer extends AppUser {
	@Transient
	private static final long serialVersionUID = 1L;

	public RecruitementOfficer() { super(); }
	
	public RecruitementOfficer(String email, String password, String firstname, String lastname) {
		super(email, password, firstname, lastname);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(ERole.RECRUITEMENT_OFFICER.name()));
		
		return authorities;
	}

}
