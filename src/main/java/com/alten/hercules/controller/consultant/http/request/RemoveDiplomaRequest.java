package com.alten.hercules.controller.consultant.http.request;

import javax.validation.constraints.NotNull;

/**
 * Class that contains the information for a request that deletes the diploma of a consultant.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class RemoveDiplomaRequest {
	
	@NotNull private Long consultant;
	@NotNull private Long diploma;
	
	public Long getConsultant() { return consultant; }
	
	public Long getDiploma() { return diploma; }

}
