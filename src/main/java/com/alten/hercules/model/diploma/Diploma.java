package com.alten.hercules.model.diploma;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Class model for a diploma.
 * @author mfoltz
 *
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Diploma {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String entitled;
	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String establishment;
	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String level;
	private int year;

	public Diploma() {}
	
	public Diploma(String entitled, String establishment, String level, int year) {
		setEntitled(entitled);
		setEstablishment(establishment);
		setLevel(level);
		setYear(year);
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getEntitled() { return entitled; }
	public void setEntitled(String entitled) { this.entitled = entitled; }

	public String getEstablishment() { return establishment; }
	public void setEstablishment(String establishment) { this.establishment = establishment; }

	public String getLevel() { return level; }
	public void setLevel(String level) { this.level = level; }
	
	public int getYear() { return year; }
	public void setYear(int year) { this.year = year; }
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		return Objects.equals(id, ((Diploma)o).id);
	}

}