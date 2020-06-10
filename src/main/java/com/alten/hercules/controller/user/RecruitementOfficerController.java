package com.alten.hercules.controller.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.controller.http.request.UpdateEntityRequest;
import com.alten.hercules.controller.user.http.request.recruitmentOfficer.AddRecruitmentOfficerRequest;
import com.alten.hercules.dal.RecruitmentOfficerDAL;
import com.alten.hercules.model.exception.InvalidFieldnameException;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.exception.ResourceNotFoundException;
import com.alten.hercules.model.exception.ResponseEntityException;
import com.alten.hercules.model.exception.UnavailableEmailException;
import com.alten.hercules.model.user.ERecruitmentOfficerFieldName;
import com.alten.hercules.model.user.RecruitmentOfficer;

@RestController
@CrossOrigin(origins="*")
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/hercules/recruitment-officers")
public class RecruitementOfficerController {
	
	@Autowired RecruitmentOfficerDAL dal;
	
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		return ResponseEntity.ok(dal.findAll());
	}
	
	@PostMapping("")
	public ResponseEntity<?> addRecruitementOfficer(@Valid @RequestBody AddRecruitmentOfficerRequest request) {
		if (!dal.emailIsAvailable(request.getEmail()))
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		RecruitmentOfficer recruitmentOfficer = request.buildUser();
		recruitmentOfficer = dal.save(recruitmentOfficer);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(recruitmentOfficer.getId());
	}
	 
	@PutMapping("")
	public ResponseEntity<?> updateRecruitementOfficer(@Valid @RequestBody UpdateEntityRequest request) {
		try {
			RecruitmentOfficer recruitmentOfficer = dal.findById(request.getId())
					.orElseThrow(() -> new ResourceNotFoundException("recruitment officer"));
			ERecruitmentOfficerFieldName fieldName;
			try { fieldName = ERecruitmentOfficerFieldName.valueOf(request.getFieldName()); }
			catch (IllegalArgumentException e) { throw new InvalidFieldnameException(); }
			switch(fieldName) {
				case firstname :
					recruitmentOfficer.setFirstname((String)request.getValue());
					break;
				case lastname :
					recruitmentOfficer.setLastname((String)request.getValue());
					break;
				case email :
					String email = (String)request.getValue();
					if (!dal.emailIsAvailable(email))
						throw new UnavailableEmailException();
					recruitmentOfficer.setEmail(email);
					break;
				case releaseDate:
					try {
						recruitmentOfficer.setReleaseDate(new SimpleDateFormat("yyyy-MM-dd").parse((String)request.getValue()));
					} catch (ParseException e) {
						throw new InvalidValueException();
					}
					break;
				default: throw new InvalidFieldnameException();
			}
			dal.save(recruitmentOfficer);
			return ResponseEntity.ok().build();
		} catch (ResponseEntityException e) { 
			return e.buildResponse();
		} catch (ClassCastException | NullPointerException e) {
			return new InvalidValueException().buildResponse();
		}
	}
	 
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRecruitementOfficer(@PathVariable Long id) {
		try {
			RecruitmentOfficer recruitmentOfficer = dal.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("recruitment officer"));
			dal.delete(recruitmentOfficer);
			return ResponseEntity
					.ok()
					.build();
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	 }
	
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getRecruitementOfficerById(@PathVariable Long id) {
		try {
			RecruitmentOfficer recruitmentOfficer = dal.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("recruitment officer"));
			return ResponseEntity.ok(recruitmentOfficer);
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	 }
}
