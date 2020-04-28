package com.alten.hercules.model.mission;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class MissionSheet {
	
	@JsonIgnore
	@EmbeddedId MissionSheetId id;
	
	@Column(nullable = true)
	private String title = null;
	
	@Column(nullable = true)
	@Length(max = 1000)
	private String description = null;
	
	@Column(nullable = true)
	private String comment = null;
	
	public MissionSheet() {}
	
	public MissionSheet(Mission mission) {
		this.id = new MissionSheetId(mission, new Date());
	}

	public MissionSheetId getId() { return id; }
	public void setId(MissionSheetId id) { this.id = id; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }
	
	@JsonGetter("date")
    private String getDate() { return new SimpleDateFormat("dd/MM/yyyy").format(id.getDate()); }

}
