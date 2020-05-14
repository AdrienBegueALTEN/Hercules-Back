package com.alten.hercules.controller.project.http.request;

import javax.validation.constraints.NotNull;

public class ProjectRequest {
	@NotNull
	private Long missionId;

	public ProjectRequest(@NotNull Long missionId) {
		super();
		this.missionId = missionId;
	}

	public ProjectRequest() {
		super();
	}

	public Long getMissionId() {
		return missionId;
	}

	public void setMissionId(Long missionId) {
		this.missionId = missionId;
	}
	
	
	
	
}
