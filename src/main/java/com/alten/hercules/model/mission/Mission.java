package com.alten.hercules.model.mission;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;

@Entity
public class Mission {
	//TODO contraintes tailles
	@Id
	private long id;
	
	private int version;
	
	private Date lastUpdate;
	
	private String title;
	
	@Length(max = 1000)
	private String description;
	
	@Enumerated(EnumType.STRING)
	private EType type;
	
	private String city;
	
	private String country;
	
	@Length(max = 250)
	private String comment;
	
	private String consultantRole;
	
	private String consultantExperience;
	@Enumerated(EnumType.STRING)
	
	private EState state;
	
	private int teamSize;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "consultant_id", nullable = false)
	@NotNull
	private Consultant consultant;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	@NotNull
	private Customer customer;

	public Mission(long id, int version, Date lastUpdate, String title, @Length(max = 1000) String description,
			EType type, String city, String country, @Length(max = 250) String comment, String consultantRole,
			String consultantExperience, EState state, int teamSize, @NotNull Consultant consultant,
			@NotNull Customer customer) {
		super();
		this.id = id;
		this.version = version;
		this.lastUpdate = lastUpdate;
		this.title = title;
		this.description = description;
		this.type = type;
		this.city = city;
		this.country = country;
		this.comment = comment;
		this.consultantRole = consultantRole;
		this.consultantExperience = consultantExperience;
		this.state = state;
		this.teamSize = teamSize;
		this.consultant = consultant;
		this.customer = customer;
	}

	public Mission(int version, Date lastUpdate, String title, @Length(max = 1000) String description, EType type,
			String city, String country, @Length(max = 250) String comment, String consultantRole,
			String consultantExperience, EState state, int teamSize, @NotNull Consultant consultant,
			@NotNull Customer customer) {
		super();
		this.version = version;
		this.lastUpdate = lastUpdate;
		this.title = title;
		this.description = description;
		this.type = type;
		this.city = city;
		this.country = country;
		this.comment = comment;
		this.consultantRole = consultantRole;
		this.consultantExperience = consultantExperience;
		this.state = state;
		this.teamSize = teamSize;
		this.consultant = consultant;
		this.customer = customer;
	}

	public Mission() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public EType getType() {
		return type;
	}

	public void setType(EType type) {
		this.type = type;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getConsultantRole() {
		return consultantRole;
	}

	public void setConsultantRole(String consultantRole) {
		this.consultantRole = consultantRole;
	}

	public String getConsultantExperience() {
		return consultantExperience;
	}

	public void setConsultantExperience(String consultantExperience) {
		this.consultantExperience = consultantExperience;
	}

	public EState getState() {
		return state;
	}

	public void setState(EState state) {
		this.state = state;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public void setTeamSize(int teamSize) {
		this.teamSize = teamSize;
	}

	public Consultant getConsultant() {
		return consultant;
	}

	public void setConsultant(Consultant consultant) {
		this.consultant = consultant;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	
	
	
}
