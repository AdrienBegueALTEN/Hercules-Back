package com.alten.hercules.model.mission.request;

import javax.validation.constraints.NotNull;

public class MissionFastRequest {
	
	@NotNull
	private Long customerId;
	
	@NotNull
	private Long consultantId;

	public MissionFastRequest() {
		super();
	}

	public MissionFastRequest(Long customerId, Long consultantId) {
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
