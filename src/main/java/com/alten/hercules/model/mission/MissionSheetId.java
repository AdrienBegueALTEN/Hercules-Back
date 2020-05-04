package com.alten.hercules.model.mission;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class MissionSheetId implements Serializable {
	
	@ManyToOne
	private Mission mission;
	private Date versionDate;
	
	public MissionSheetId() {}
	
	public MissionSheetId(Mission mission, Date versionDate) {
		this.mission = mission;
		this.versionDate = versionDate;
	}

	public Date getVersionDate() { return versionDate; }
}
