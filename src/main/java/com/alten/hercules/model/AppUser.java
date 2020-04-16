package com.alten.hercules.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class AppUser {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 64)
	@Email
	private String email;

	@JsonIgnore
	@NotBlank
	private String password;
	
	@Size(min = 2, max = 32)
	private String firstname;
	
	@Size(min = 2, max = 32)
	private String lastname;
	
	@Column(nullable = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private Date releaseDate;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_role", 
				joinColumns = @JoinColumn(name = "user_id"), 
				inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
	public AppUser() {}
	
	public AppUser(String email, String password, String firstname, String lastname, Set<Role> roles) {
		this.email = email;
		this.password = new BCryptPasswordEncoder().encode(password);
		this.firstname = firstname;
		this.lastname = lastname;
		this.roles = roles;
		this.releaseDate = null;
	}
	
	public AppUser(String email, String password, String firstname, String lastname, Set<Role> roles, Date releaseDate) {
		this.email = email;
		this.password = new BCryptPasswordEncoder().encode(password);
		this.firstname = firstname;
		this.lastname = lastname;
		this.roles = roles;
		this.releaseDate = releaseDate;
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = new BCryptPasswordEncoder().encode(password); }
	
	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }
	
	public Set<Role> getRoles() { return roles; }
	public void setRoles(Set<Role> roles) { this.roles = roles; }

	public Date getReleaseDate() { return releaseDate; }
	public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

}
