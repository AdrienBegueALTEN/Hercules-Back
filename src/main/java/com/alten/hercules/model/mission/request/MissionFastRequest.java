package com.alten.hercules.model.mission.request;

import javax.validation.constraints.NotBlank;

public class MissionFastRequest {
	@NotBlank
	private Long customerId;
	
	@NotBlank
	private Long consultantId;
	
	

	public MissionFastRequest() {
		super();
	}

	public MissionFastRequest(@NotBlank Long customerId, @NotBlank Long consultantId) {
		super();
		this.customerId = customerId;
		this.consultantId = consultantId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getConsultantId() {
		return consultantId;
	}

	public void setConsultantId(Long consultantId) {
		this.consultantId = consultantId;
	}
	
	
}
