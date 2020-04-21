package com.alten.hercules.model.diploma;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class Diploma {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private int graduationYear;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("id")
	private DiplomaLocation diplomaLocation;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("id")
	private DiplomaName diplomaName;

	public Diploma(int graduationYear, DiplomaLocation diplomaLocation, DiplomaName diplomaName) {
		super();
		this.graduationYear = graduationYear;
		this.diplomaLocation = diplomaLocation;
		this.diplomaName = diplomaName;
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
