package com.alten.hercules.model.mission;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class MissionSheetId implements Serializable {
	
	@ManyToOne
	private Mission mission;
	private Date date;
	
	public MissionSheetId() {}
	
	public MissionSheetId(Mission mission, Date date) {
		this.mission = mission;
		this.date = date;
	}

	public Date getDate() { return date; }
}
