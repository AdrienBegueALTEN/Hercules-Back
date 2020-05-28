package com.alten.hercules.model.user;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Inheritance
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class AppUser implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@JsonIgnore
	@Column(nullable = true)
	private String password;
	
	@Column(nullable = false)
	private String firstname;
	
	@Column(nullable = false)
	private String lastname;
	
	@Column(nullable = true)
	private Date releaseDate;
	
	public AppUser() {}
	
	public AppUser(String email, String password, String firstname, String lastname) {
		setEmail(email);
		setPassword(password);
		setFirstname(firstname);
		setLastname(lastname);
		setReleaseDate(null);
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = new BCryptPasswordEncoder().encode(password); }
	public void expireCredentials() { this.password = null; }
	
	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }

	public Date getReleaseDate() { return releaseDate; }
	public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

	@JsonIgnore
	@Override
	public String getUsername() { return email; }
	
	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() { return true; }

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() { return true; }

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() { return password != null; }

	@JsonIgnore
	@Override
	public boolean isEnabled() { return releaseDate == null; }
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		AppUser user = (AppUser) o;
		return Objects.equals(id, user.id);
	}
}
