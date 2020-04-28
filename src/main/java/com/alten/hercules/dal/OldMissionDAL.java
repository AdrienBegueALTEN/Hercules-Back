package com.alten.hercules.dal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.customer.CustomerDAO;
import com.alten.hercules.dao.mission.OldMissionDAO;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.OldMission;

@Service
public class OldMissionDAL {
	@Autowired
	OldMissionDAO missionDAO;
	
	@Autowired
	ConsultantDAO consultantDAO;
	
	@Autowired
	CustomerDAO customerDAO;
	
	public boolean existsConsultant(Long id) {
		return this.consultantDAO.findById(id)!=null;
	}
	
	public boolean existsCustomer(Long id) {
		return this.customerDAO.findById(id).isPresent();
	}
	
	public Optional<Consultant> findConsultantById(Long id) {
		return consultantDAO.findById(id);
	}
	
	public Optional<Customer> findCustomerById(Long id) {
		return customerDAO.findById(id);
	}
	
	public List<OldMission> findAll(){
		return this.missionDAO.findAll();
	}
	
	public Optional<OldMission> findMissionById(Long id) {
		return missionDAO.findById(id);
	}
	
	public List<OldMission> byReference(Long reference){
		return this.missionDAO.byReference(reference);
	}
	
	public Optional<OldMission> lastVersionByReference(Long reference) {
		return this.missionDAO.lastVersionByReference(reference);
	}
	
	public OldMission save(OldMission mission) {
		return this.missionDAO.save(mission);
	}
	
	public List<OldMission> allMissionLastUpdate(){
		return this.missionDAO.allMissionLastUpdate();
	}
	
	public void delete(OldMission mission) {
		this.missionDAO.delete(mission);
	}

	public boolean missionExistsById(Long id) {
		return missionDAO.existsById(id);
	}
}
