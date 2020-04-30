package com.alten.hercules.controller.diploma.http.request;

import javax.validation.constraints.NotNull;

public class DeleteDiplomaRequest {
	@NotNull
	public Long consultantId;
	
	@NotNull
	public Long diplomaId;
	
	
	
	public DeleteDiplomaRequest() {
		super();
	}
	public DeleteDiplomaRequest(@NotNull Long consultantId, @NotNull Long diplomaId) {
		super();
		this.consultantId = consultantId;
		this.diplomaId = diplomaId;
	}
	public Long getConsultantId() {
		return consultantId;
	}
	public void setConsultantId(Long consultantId) {
		this.consultantId = consultantId;
	}
	public Long getDiplomaId() {
		return diplomaId;
	}
	public void setDiplomaId(Long diplomaId) {
		this.diplomaId = diplomaId;
	}
	
	
}
