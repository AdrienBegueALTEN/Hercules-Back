package com.alten.hercules.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.user.UserDAO;

@Service
public class GRDPDAL {
	
	@Autowired private ConsultantDAO consultantDAO;
	@Autowired private UserDAO userDAO;
	
	public void applyGRDP() {
		consultantDAO.applyGRDP();
		userDAO.applyGRDP();
	}

}
