package com.alten.hercules.model.diploma;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Diploma {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int graduationYear;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "diploma_location_id", nullable = false)
	private DiplomaLocation diplomaLocation;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "diploma_name_id", nullable = false)
	private DiplomaName diplomaName;

	public Diploma(int graduationYear, DiplomaLocation diplomaLocation, DiplomaName diplomaName) {
		super();
		this.graduationYear = graduationYear;
		this.diplomaLocation = diplomaLocation;
		this.diplomaName = diplomaName;
	}
	
	public Diploma() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public int getGraduationYear() { return graduationYear; }
	public void setGraduationYear(int graduationYear) { this.graduationYear = graduationYear; }

	public DiplomaLocation getDiplomaLocation() { return diplomaLocation; }
	public void setDiplomaLocation(DiplomaLocation diplomaLocation) { this.diplomaLocation = diplomaLocation; }

	public DiplomaName getDiplomaName() { return diplomaName; }
	public void setDiplomaName(DiplomaName diplomaName) { this.diplomaName = diplomaName; }
}