package com.alten.hercules.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.user.UserDAO;

/**
 * Layer to access the DAL needed to apply GRPD.
 * @author rjesson, mdoltz, abegue, jbaudot
 *
 */
@Service
public class GRDPDAL {
	
	@Autowired private ConsultantDAO consultantDAO;
	@Autowired private UserDAO userDAO;
	
	/**
	 * Apply GRDP to the users and consultants.
	 */
	public void applyGRDP() {
		consultantDAO.applyGRDP();
		userDAO.applyGRDP();
	}

}
