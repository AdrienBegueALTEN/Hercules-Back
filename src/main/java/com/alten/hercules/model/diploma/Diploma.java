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
	
	/**
	 * ID of the diploma
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * Title of the diploma
	 */
	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String entitled;
	
	/**
	 * Establishment that gave the diploma
	 */
	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String establishment;
	
	/**
	 * Level of the diploma
	 */
	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String level;
	
	/**
	 * Year of obtainment of the diploma
	 */
	private int year;
	
	/**
	 * Empty constructor
	 */
	public Diploma() {}
	
	/**
	 * Constructor
	 */
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