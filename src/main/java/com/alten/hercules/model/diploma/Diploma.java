package com.alten.hercules.model.diploma;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.alten.hercules.model.consultant.Consultant;

@Entity
public class Diploma {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Date graduationDate;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("id")
	private DiplomaLocation diplomaLocation;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("id")
	private DiplomaName diplomaName;
	

	public Diploma(Date graduationDate, DiplomaLocation diplomaLocation, DiplomaName diplomaName) {
		super();
		this.graduationDate = graduationDate;
		this.diplomaLocation = diplomaLocation;
		this.diplomaName = diplomaName;
	}
	
	
	
	
}
