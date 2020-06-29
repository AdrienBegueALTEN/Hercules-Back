package com.alten.hercules.model.user;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;

import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.alten.hercules.model.exception.InvalidValueException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Class model for an user.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Entity
@Inheritance
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class AppUser implements UserDetails {
	
	/**
	 * ID of the user
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * Email of the user
	 */
	@Column(nullable = false)
	private String email;
	
	/**
	 * Password of the user
	 */
	@JsonIgnore
	@Column(nullable = true)
	private String password;
	
	/**
	 * First name of the user
	 */
	@Column(nullable = false)
	private String firstname;
	
	/**
	 * Last name of the user
	 */
	@Column(nullable = false)
	private String lastname;
	
	/**
	 * Date of release of the user
	 */
	@Column(nullable = true)
	private LocalDate releaseDate;
	
	/**
	 * Secret of the user
	 */
	@JsonIgnore
	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer secret;
	
	/**
	 * Empty constructor
	 */
	public AppUser() {}
	
	/**
	 * Complete constructor
	 */
	public AppUser(String email, String firstname, String lastname) throws InvalidValueException {
		setEmail(email);
		setFirstname(firstname);
		setLastname(lastname);
		setReleaseDate(null);
		changeSecret();
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	
	public String getPassword() { return password; }
	public void setPassword(String password) throws InvalidValueException {
		if (password == null) this.password = null;
		else {
			if (password.equals("")) throw new InvalidValueException();
			this.password = new BCryptPasswordEncoder().encode(password);
			changeSecret();
		}
	}
	
	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }

	public LocalDate getReleaseDate() { return releaseDate; }
	public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
	
	public int getSecret() { return secret; }
	private void changeSecret() {
		this.secret = (int)Math.floor(Math.random() * Math.floor(Integer.MAX_VALUE));
	}

	@JsonIgnore
	@Override
	public String getUsername() { return email; }
	
	
	/**
	 * Verifies if the account is not expired
	 */
	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() { return true; }
	
	/**
	 * Verifies if the account is not locked
	 */
	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() { return true; }
	
	/**
	 * Verifies if the identifiers are valid
	 */
	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() { return password != null; }
	
	/**
	 * Verifies if the user is active
	 */
	@JsonIgnore
	@Override
	public boolean isEnabled() { 
		if (releaseDate == null) return true;
		LocalDate today = LocalDate.now();
		return (today.getYear() <= releaseDate.getYear() && today.getDayOfYear() < releaseDate.getDayOfYear());
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		AppUser user = (AppUser) o;
		return Objects.equals(id, user.getId());
	}
}
