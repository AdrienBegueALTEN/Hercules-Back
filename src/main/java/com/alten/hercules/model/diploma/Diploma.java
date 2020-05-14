package com.alten.hercules.model.diploma;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Diploma {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String entitled;
	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String etablishment;
	@Column(nullable = false, columnDefinition = "VARCHAR(100) default ''")
	private String level;
	private int year;

	public Diploma() {}
	
	public Diploma(String entitled, String etablishment, String level, int year) {
		setEntitled(entitled);
		setEtablishment(etablishment);
		setLevel(level);
		setYear(year);
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getEntitled() { return entitled; }
	public void setEntitled(String entitled) { this.entitled = entitled; }

	public String getEtablishment() { return etablishment; }
	public void setEtablishment(String etablishment) { this.etablishment = etablishment; }

	public String getLevel() { return level; }
	public void setLevel(String level) { this.level = level; }
	
	public int getYear() { return year; }
	public void setYear(int year) { this.year = year; }

}