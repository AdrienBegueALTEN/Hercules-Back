package com.alten.hercules.controller.consultant.http.request;

import javax.validation.constraints.NotNull;

public class RemoveDiplomaRequest {
	
	@NotNull private Long consultant;
	@NotNull private Long diploma;
	
	public Long getConsultant() { return consultant; }
	
	public Long getDiploma() { return diploma; }

}
