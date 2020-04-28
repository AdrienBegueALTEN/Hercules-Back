package com.alten.hercules.controller.user.http.request.recruitementOfficer;

import com.alten.hercules.controller.user.http.request.AddUserRequest;
import com.alten.hercules.model.user.RecruitementOfficer;

public class AddRecruitementOfficerRequest extends AddUserRequest {
	
	public AddRecruitementOfficerRequest(String email, String password, String firstname, String lastname) {
		super(email, password, firstname, lastname);
	}

	@Override
	public RecruitementOfficer buildUser() {
		return new RecruitementOfficer(email, password, firstname, lastname);
	}

}
