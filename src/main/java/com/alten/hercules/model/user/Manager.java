package com.alten.hercules.model.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.alten.hercules.model.consultant.Consultant;

@Entity
public class Manager extends AppUser {
	@Transient
	private static final long serialVersionUID = 1L;
	
	private boolean isAdmin = false;
	
    @OneToMany(mappedBy="manager")
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
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(ERole.MANAGER.name()));
		if (isAdmin) authorities.add(new SimpleGrantedAuthority(ERole.ADMIN.name()));
		
		return authorities;
	}

	public boolean isAdmin() { return isAdmin; }
	public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
	
	public Set<Consultant> getConsultants() { return consultants; }
	public void setConsultants(Set<Consultant> consultants) { this.consultants = consultants; }

}
