package com.alten.hercules.dal;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.customer.CustomerDAO;
import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.dao.mission.MissionSheetDAO;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;

@Service
public class MissionDAL {
	
	@Autowired private MissionDAO missionDAO;
	@Autowired private MissionSheetDAO sheetDAO;
	@Autowired private ConsultantDAO consultantDAO;
	@Autowired private CustomerDAO customerDAO;
	
	public Optional<Mission> findById(Long id) {
		return missionDAO.findById(id);
	}
	
	public Optional<Consultant> findConsultantById(Long id) {
		return consultantDAO.findById(id);
	}
	
	public Optional<Customer> findCustomerById(Long id) {
		return customerDAO.findById(id);
	}
	
	public void save(Mission mission) {
		missionDAO.save(mission);
	}
	
	public void saveSheet(MissionSheet sheet) {
		sheetDAO.save(sheet);
	}

}
