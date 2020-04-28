package com.alten.hercules.model.mission;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.validator.constraints.Length;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.request.UpdateMissionRequest;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class OldMission {
	//TODO contraintes tailles
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false)
	private Date lastUpdate;
	
	@Column(nullable = true)
	private String title;
	
	@Column(nullable = true)
	@Length(max = 1000)
	private String description;
	
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private EType type;
	
	@Column(nullable = true)
	private String city;
	
	@Column(nullable = true)
	private String country;
	
	@Column(nullable = true)
	@Length(max = 250)
	private String comment;
	
	@Column(nullable = true)
	private String consultantRole;
	
	@Column(columnDefinition = "integer default 0")
	private int consultantExperience;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ESheetStatus state;
	
	@Column(columnDefinition = "integer default 0")
	private int teamSize;
	
	@Column(nullable = true)
	private Long reference;
	
	@JsonIgnore
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "consultant_id", nullable = false)
	private Consultant consultant;
	
	@JsonIgnore
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;
	
	public OldMission(Consultant consultant, Customer customer) {
		this.consultant = consultant;
		this.customer = customer;
		this.state = ESheetStatus.WAITING;
		this.lastUpdate = new Date();
	}

	public OldMission(long id, Date lastUpdate, String title, @Length(max = 1000) String description,
			EType type, String city, String country, @Length(max = 250) String comment, String consultantRole,
			int consultantExperience, ESheetStatus state, int teamSize, Long reference, Consultant consultant,
			Customer customer) {
		super();
		this.id = id;
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
		this.reference = reference;
		this.consultant = consultant;
		this.customer = customer;
	}

	public OldMission(Date lastUpdate, String title, @Length(max = 1000) String description, EType type,
			String city, String country, @Length(max = 250) String comment, String consultantRole,
			int consultantExperience, ESheetStatus state, int teamSize, Long reference, Consultant consultant,
			Customer customer) {
		super();
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
		this.reference = reference;
		this.consultant = consultant;
		this.customer = customer;
	}
	
	public OldMission(OldMission other) {
		this(other.lastUpdate,
				other.title,
				other.description,
				other.type,
				other.city,
				other.country,
				other.comment,
				other.consultantRole,
				other.consultantExperience,
				other.state,
				other.teamSize,
				other.reference,
				other.consultant,
				other.customer);
	}

	public OldMission() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getReference() {
		return reference;
	}

	public void setReference(Long reference) {
		this.reference = reference;
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

	public int getConsultantExperience() {
		return consultantExperience;
	}

	public void setConsultantExperience(int consultantExperience) {
		this.consultantExperience = consultantExperience;
	}

	public ESheetStatus getState() {
		return state;
	}

	public void setState(ESheetStatus state) {
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
	
	@JsonGetter("customerId")
    private Long getConsultantId() {
		Long customerId=null;
        if (this.customer != null)
            customerId = this.customer.getId();
        return customerId;
    }
	
	@JsonGetter("consultantId")
    private Long getCustomerId() {
		Long consultantId=null;
        if (this.consultant != null)
        	consultantId = this.consultant.getId();
        return consultantId;
    }
	
	public static void setMissionParameters(OldMission mission, UpdateMissionRequest req) {
		if (req.getTitle() != null && !req.getTitle().isEmpty())
			mission.setTitle(req.getTitle());

		if (req.getDescription() != null && !req.getDescription().isEmpty())
			mission.setDescription(req.getDescription());

		if (req.getType() != null)
			mission.setType(req.getType());

		if (req.getCity() != null && !req.getCity().isEmpty())
			mission.setCity(req.getCity());

		if (req.getCountry() != null && !req.getCountry().isEmpty())
			mission.setCountry(req.getCountry());

		if (req.getComment() != null && !req.getComment().isEmpty())
			mission.setComment(req.getComment());

		if (req.getConsultantRole() != null && !req.getConsultantRole().isEmpty())
			mission.setConsultantRole(req.getConsultantRole());

		if (req.getConsultantExperience() != null)
			mission.setConsultantExperience(req.getConsultantExperience());

		if (req.getState() != null)
			mission.setState(req.getState());

		if (req.getTeamSize() != null)
			mission.setTeamSize(req.getTeamSize());

	}
	
}
