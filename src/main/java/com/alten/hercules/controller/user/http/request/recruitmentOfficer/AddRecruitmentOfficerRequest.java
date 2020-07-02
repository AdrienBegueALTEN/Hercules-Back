package com.alten.hercules.controller.user.http.request.recruitmentOfficer;

import com.alten.hercules.controller.user.http.request.AddUserRequest;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.user.RecruitmentOfficer;

/**
 * Class that contains the information for a request that adds a recruitment's officer
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class AddRecruitmentOfficerRequest extends AddUserRequest {
	
	public AddRecruitmentOfficerRequest(String email, String firstname, String lastname) {
		super(email, firstname, lastname);
	}

	@Override
	public RecruitmentOfficer buildUser() throws InvalidValueException {
		return new RecruitmentOfficer(email, firstname, lastname);
	}

}
