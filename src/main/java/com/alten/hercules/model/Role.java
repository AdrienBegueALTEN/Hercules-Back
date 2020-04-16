package com.alten.hercules.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Role {
	
	@Id
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(length = 16, unique = true)
	private ERole name;

	public Role() {}
	public Role(ERole name) { this.name = name; }

	public Integer getId() { return this.id; }
	public void setId(Integer id) { this.id = id; }

	public ERole getName() { return this.name; }
	public void setName(ERole name) { this.name = name; }
	
}