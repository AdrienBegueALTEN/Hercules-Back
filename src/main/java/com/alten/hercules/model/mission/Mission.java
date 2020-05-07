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
import javax.persistence.OrderBy;

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
	
	@OneToMany(mappedBy="mission")
	@OrderBy("version_date DESC")
	private Set<MissionSheet> versions = new HashSet<>();
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ESheetStatus sheetStatus;
	
	@JsonIgnore
	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer secret;
	
	public Mission() {}
	
	public Mission(Consultant consultant, Customer customer) {
		setConsultant(consultant);
		setCustomer(customer);
		setSheetStatus(ESheetStatus.ON_WAITING);
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

	public ESheetStatus getSheetStatus() { return sheetStatus; }
	public void setSheetStatus(ESheetStatus sheetStatus) { this.sheetStatus = sheetStatus; }
	
	public int getSecret() { return secret; }
	public void changeSecret() {
		this.secret = (int)Math.floor(Math.random() * Math.floor(Integer.MAX_VALUE));
	}
	
	@JsonGetter("consultant")
    private Long getConsultantId() {
        return consultant.getId();
    }
	
	@JsonGetter("customer")
    private Long getCustomerId() {
        return customer.getId();
    }
}
