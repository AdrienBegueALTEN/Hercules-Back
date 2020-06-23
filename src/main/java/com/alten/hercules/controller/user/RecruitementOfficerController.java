package com.alten.hercules.controller.user;

import java.time.LocalDate;

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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Class that manages the requests sent to the API for the recruitment officers.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@RestController
@CrossOrigin(origins="*")
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/hercules/recruitment-officers")
public class RecruitementOfficerController {
	
	@Autowired RecruitmentOfficerDAL dal;
	
	/**
	 * Function that gives back the information of all the recruitment officers in the database.
	 * @return 200 The details of all the recruitment officers are given back<br>401 Authentication problem.
	 */
	@ApiOperation(
			value = "Get all the recruitment officers.",
			notes = "Return all the informations of all the recruitment officers."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message="OK."),
		@ApiResponse(code = 401, message="Invalid authentification token.")
	})
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		return ResponseEntity.ok(dal.findAll());
	}
	
	/**
	 * Function that adds a recruitment officer using the information in the request.
	 * @param request Request that contains the email and names of the new recruitment officer
	 * @return 201 The recruitment officer is added<br>401 Authentication problem<br>400 The given values are not valid<br>404 The recruitment officer is not found<br>409 The email is already used.
	 */
	@ApiOperation(
			value = "Create a recruitment officer.",
			notes = "Add a new recruitment officer in the database with the given information."
	)
	@ApiResponses({
		@ApiResponse(code = 201, message="Recruitment officer was created."),
		@ApiResponse(code = 401, message="Invalid authentification token."),
		@ApiResponse(code = 409, message="Email address is not available."),
		@ApiResponse(code = 404, message="Recruitment officer is not found."),
		@ApiResponse(code = 400, message="Bad format for email or names")
	})
	@PostMapping("")
	public ResponseEntity<?> addRecruitementOfficer(@ApiParam(
						"email : user's email;\n"
						+ "firstname : user's firstname;\n"
						+ "lastname : user's lastname;\n"
					)
					@Valid @RequestBody AddRecruitmentOfficerRequest request) {
		if (!dal.emailIsAvailable(request.getEmail()))
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		RecruitmentOfficer recruitmentOfficer = request.buildUser();
		recruitmentOfficer = dal.save(recruitmentOfficer);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(recruitmentOfficer.getId());
	}
	
	/**
	 * Function that modifies the specific field of a specific recruitment officer using the information in the request.
	 * @param request Request that contains the field's name, the modified value and the id of the recruitment officer
	 * @return 200 The recruitment officer is updated<br>400 The given details are invalid<br>401 Authentication problem<br>404 The recruitment officer is not found<br>409 The email is already used.
	 */
	@ApiOperation(
			value = "Modify a recruitment officer.",
			notes = "Modify the specified field of the recruitment officer with the the given value."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message="Recruitment officer was modified."),
		@ApiResponse(code = 401, message="Invalid authentification token."),
		@ApiResponse(code = 409, message="Email address is not available."),
		@ApiResponse(code = 404, message="Recruitment officer is not found."),
		@ApiResponse(code = 400, message="Bad fieldname or value")
	})
	@PutMapping("")
	public ResponseEntity<?> updateRecruitementOfficer(@ApiParam(
			"id : recruitment officer's id;\n"
			+ "fieldname : name of the field to modify;\n"
			+ "value : modified value;\n"
			)@Valid @RequestBody UpdateEntityRequest request) {
		try {
			RecruitmentOfficer recruitmentOfficer = dal.findById(request.getId())
					.orElseThrow(() -> new ResourceNotFoundException(RecruitmentOfficer.class));
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
					if(request.getValue()==null) 
						recruitmentOfficer.setReleaseDate(null);
					else
						recruitmentOfficer.setReleaseDate(LocalDate.parse((String)request.getValue()));
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
	 
	/**
	 * Function that deletes a specific recruitment officer.
	 * @param id ID of the recruitment officer
	 * @return 200 The recruitment officer is removed<br>401 Authentication problem<br>404 The recruitment officer is not found.
	 */
	@ApiOperation(
			value = "Delete a recruitment officer.",
			notes = "Delete the recruitment officer from the database."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message="Recruitment officer was deleted."),
		@ApiResponse(code = 401, message="Invalid authentification token."),
		@ApiResponse(code = 404, message="Recruitment officer is not found.")
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRecruitementOfficer(@ApiParam("Recruitment officer's id.")
													   @PathVariable Long id) {
		try {
			RecruitmentOfficer recruitmentOfficer = dal.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(RecruitmentOfficer.class));
			dal.delete(recruitmentOfficer);
			return ResponseEntity
					.ok()
					.build();
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	 }
	
	/**
	 * Function that gives back the information of a specific recruitment officer.
	 * @param id ID of the recruitment officer
	 * @return 200 The details of the recruitment officer are given back<br>401 Authentication problem<br>404 The recruitment officer is not found.
	 */
	@ApiOperation(
			value = "Get a specific recruitment officer.",
			notes = "Return the information of a specific recruitment officer."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message="OK."),
		@ApiResponse(code = 401, message="Invalid authentification token."),
		@ApiResponse(code = 404, message="Recruitment officer is not found.")
	})
	@GetMapping("/{id}")
	public ResponseEntity<?> getRecruitementOfficerById(@ApiParam("Recruitment officer's id.")
														@PathVariable Long id) {
		try {
			RecruitmentOfficer recruitmentOfficer = dal.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(RecruitmentOfficer.class));
			return ResponseEntity.ok(recruitmentOfficer);
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	 }
}
