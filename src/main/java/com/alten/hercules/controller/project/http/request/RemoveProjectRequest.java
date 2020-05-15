package com.alten.hercules.controller.project.http.request;

import javax.validation.constraints.NotNull;

public class RemoveProjectRequest {
	@NotNull private Long missionId;
	@NotNull private Long projectId;
	
	public RemoveProjectRequest() { super(); }
	
	public RemoveProjectRequest(@NotNull Long missionId, @NotNull Long projectId) {
		super();
		this.missionId = missionId;
		this.projectId = projectId;
	}
	
	public Long getMissionId() {return missionId;}
	public void setMissionId(Long missionId) {this.missionId = missionId;}
	
	public Long getProjectId() {return projectId;}
	public void setProjectId(Long projetcId) {this.projectId = projetcId;}
	
	
}
