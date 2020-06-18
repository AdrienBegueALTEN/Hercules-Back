package com.alten.hercules.controller.consultant.http.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.alten.hercules.model.diploma.Diploma;

/**
 * Class that contains the information for a request that adds a diploma to a consultant.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class AddDiplomaRequest {
	
	@NotNull private Long consultant;
	@NotBlank private String establishment;
	@NotBlank private String entitled;
	@NotBlank private String level;
	@NotNull private Integer year;
	
	public AddDiplomaRequest(Long consultant, String establishment, String entitled, String level, Integer year) {
		setConsultant(consultant);
		setEstablishment(establishment);
		setEntitled(entitled);
		setLevel(level);
		setYear(year);
	}
	
	public Long getConsultant() { return consultant; }
	public void setConsultant(Long consultant) { this.consultant = consultant; }
	
	public String getEstablishment() { return establishment; }
	public void setEstablishment(String establishment) { this.establishment = establishment; }
	
	public String getEntitled() { return entitled; }
	public void setEntitled(String entitled) { this.entitled = entitled; }
	
	public String getLevel() { return level; }
	public void setLevel(String level) { this.level = level; }
	
	public Integer getYear() { return year; }
	public void setYear(Integer year) { this.year = year; }
	
	/**
	 * Function that produces an Object Diploma by using its fields.
	 * @return
	 */
	public Diploma buildDiploma() {
		return new Diploma(entitled, establishment, level, year);
	}

}