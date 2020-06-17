package com.alten.hercules.controller.user.http.request.recruitmentOfficer;

import com.alten.hercules.controller.user.http.request.AddUserRequest;
import com.alten.hercules.model.user.RecruitmentOfficer;

public class AddRecruitmentOfficerRequest extends AddUserRequest {
	
	public AddRecruitmentOfficerRequest(String email, String firstname, String lastname) {
		super(email, firstname, lastname);
	}

	@Override
	public RecruitmentOfficer buildUser() {
		return new RecruitmentOfficer(email, firstname, lastname);
	}

}
