package com.alten.hercules.dal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.customer.CustomerDAO;
import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.Mission;

@Service
public class MissionDAL {
	@Autowired
	MissionDAO missionDAO;
	
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
	
	public List<Mission> findAll(){
		return this.missionDAO.findAll();
	}
	
	public Optional<Mission> findById(Long id) {
		return missionDAO.findById(id);
	}
	
	public List<Mission> byReference(Long reference){
		return this.missionDAO.byReference(reference);
	}
	
	public Optional<Mission> lastVersionByReference(Long reference) {
		return this.missionDAO.lastVersionByReference(reference);
	}
	
	public Mission save(Mission mission) {
		return this.missionDAO.save(mission);
	}
	
	public List<Mission> allMissionLastUpdate(){
		return this.missionDAO.allMissionLastUpdate();
	}
	
	public void delete(Mission mission) {
		this.missionDAO.delete(mission);
	}
}
