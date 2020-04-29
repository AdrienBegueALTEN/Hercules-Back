package com.alten.hercules.model.mission;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;

import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Mission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Consultant consultant;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Customer customer;
	
	@OneToMany(mappedBy="id.mission")
	private Set<MissionSheet> versions = new HashSet<>();
	
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private EType type;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ESheetStatus sheetStatus;
	
	@Column(nullable = true)
	private String city;
	
	@Column(nullable = true)
	private String country;
	
	@Min(0)
	@Column(nullable = true)
	private Integer consultantStartExp;
	
	@Min(1)
	@Column(nullable = true)
	private Integer teamSize;
	
	public Mission() {}
	
	public Mission(Consultant consultant, Customer customer) {
		this.consultant = consultant;
		this.customer = customer;
		this.sheetStatus = ESheetStatus.WAITING;
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public Consultant getConsultant() { return consultant; }
	public void setConsultant(Consultant consultant) { this.consultant = consultant; }

	public Customer getCustomer() { return customer; }
	public void setCustomer(Customer customer) { this.customer = customer; }

	public Set<MissionSheet> getVersions() { return versions; }
	public void setVersions(Set<MissionSheet> versions) { this.versions = versions; }
	public void addVersion(MissionSheet version) { this.versions.add(version); }

	public EType getType() { return type; }
	public void setType(EType type) { this.type = type; }

	public ESheetStatus getSheetStatus() { return sheetStatus; }
	public void setSheetStatus(ESheetStatus sheetStatus) { this.sheetStatus = sheetStatus; }

	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }

	public String getCountry() { return country; }
	public void setCountry(String country) { this.country = country; }

	public Integer getConsultantStartExp() { return consultantStartExp; }
	public void setConsultantStartExp(int consultantStartExp) { this.consultantStartExp = consultantStartExp; }

	public Integer getTeamSize() { return teamSize; }
	public void setTeamSize(int teamSize) { this.teamSize = teamSize; }
	
	@JsonGetter("consultant")
    private Long getConsultantId() {
        return consultant.getId();
    }
	
	@JsonGetter("customer")
    private Long getCustomerId() {
        return customer.getId();
    }
}
