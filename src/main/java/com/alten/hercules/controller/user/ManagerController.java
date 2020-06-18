package com.alten.hercules.controller.user;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.controller.http.request.UpdateEntityRequest;
import com.alten.hercules.controller.user.http.request.manager.AddManagerRequest;
import com.alten.hercules.dal.ManagerDAL;
import com.alten.hercules.model.exception.EntityDeletionException;
import com.alten.hercules.model.exception.InvalidFieldnameException;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.exception.ResourceNotFoundException;
import com.alten.hercules.model.exception.ResponseEntityException;
import com.alten.hercules.model.exception.UnavailableEmailException;
import com.alten.hercules.model.user.EManagerFieldName;
import com.alten.hercules.model.user.Manager;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/managers")
public class ManagerController {
	
	@Autowired private ManagerDAL dal;
	
	@ApiOperation(
			value = "Get a manager.",
			notes = "Return all informations related to the manager."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message="OK."),
		@ApiResponse(code = 401, message="Invalid authentification token."),
		@ApiResponse(code = 403, message="User isn't an administrator."),
		@ApiResponse(code = 404, message="Manager not found.")
	})
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/{managerId}")
	public ResponseEntity<?> getManager(
			@ApiParam("Manager's identifier.")
			@PathVariable Long managerId) {
		try {
			Manager manager = dal.findById(managerId)
				.orElseThrow(() -> new ResourceNotFoundException(Manager.class));
			Map <String, Object> body = 
					new ManagerResponseBodyBuilder(manager)
						.id()
						.firstname()
						.lastname()
						.email()
						.admin()
						.consultants()
						.releaseDate()
						.build();
			return ResponseEntity.ok(body);
		} catch (ResponseEntityException e) { return e.buildResponse(); }
	 }
	
	@ApiOperation(
			value = "Get all managers.",
			notes = "Return the list of all the managers."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message="OK."),
		@ApiResponse(code = 401, message="Invalid authentification token."),
		@ApiResponse(code = 403, message="User isn't a manager.")
	})
	@PreAuthorize("hasAuthority('MANAGER')")
	@GetMapping
	public ResponseEntity<?> getAll(
			@ApiParam("Indicates if only active managers should be returned.\n"
					+ "False if isn't specified.")
			@RequestParam Optional<Boolean> onlyActive) {
		return ResponseEntity.ok(onlyActive.orElse(false) ? dal.findAllActive() : dal.findAll());
	}
	
	@ApiOperation(
			value = "Create a manager.",
			notes = "Add a new manager user in database."
	)
	@ApiResponses({
		@ApiResponse(code = 201, message="Manager created."),
		@ApiResponse(code = 400, message="Wrong format for the firstname, lastname or email."),
		@ApiResponse(code = 401, message="Invalid authentification token."),
		@ApiResponse(code = 403, message="User isn't an administrator."),
		@ApiResponse(code = 404, message="Manager not found."),
		@ApiResponse(code = 409, message="Email isn't available.")
	})
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping
	public ResponseEntity<?> addManager(
			@ApiParam(
				"email : user's email;\n"
				+ "firstname : user's firstname;\n"
				+ "lastname : user's lastname;\n"
				+ "isAdmin : indicates if the manager has the admin rights."
			)
			@Valid @RequestBody AddManagerRequest request) {
		try {
			if (!dal.emailIsAvailable(request.getEmail())) throw new UnavailableEmailException();
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(dal.save(request.buildUser()).getId());
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	}
	 
	@ApiOperation(
			value="Update a manager's field value.",
			notes="Update the value of one of the fields of a manager."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message="Manager's field value updated."),
		@ApiResponse(code = 400, message="Inexistant fieldname or invalid value."),
		@ApiResponse(code = 401, message="Invalid authentification token."),
		@ApiResponse(code = 403, message="User isn't an administrator."),
		@ApiResponse(code = 404, message="Manager not found."),
		@ApiResponse(code = 409, message="Email isn't available.")
	})
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping
	public ResponseEntity<?> updateManager(
			@ApiParam(
					"id : manager's identifier;\n"
					+ "fieldname : field's name to update;\n"
					+ "value : field's new value."
			)
			@Valid @RequestBody UpdateEntityRequest request) {
		try {
			Manager manager = dal.findById(request.getId())
					.orElseThrow(() -> new ResourceNotFoundException(Manager.class));
			switch(EManagerFieldName.valueOf(request.getFieldName())) {
				case firstname :
					manager.setFirstname((String)request.getValue());
					break;
				case lastname :
					manager.setLastname((String)request.getValue());
					break;
				case email :
					String email = (String)request.getValue();
					if (!dal.emailIsAvailable(email))
						throw new UnavailableEmailException();
					manager.setEmail(email);
					break;
				case releaseDate:
					if(request.getValue()==null) 
						manager.setReleaseDate(null);
					else {
						manager.setReleaseDate(LocalDate.parse((String)request.getValue()));
						manager.setAdmin(false);
					}
					break;
				case isAdmin:
					manager.setAdmin((Boolean)request.getValue());
					break;
				default: throw new InvalidFieldnameException();
			}
			dal.save(manager);
			return ResponseEntity.ok().build();
		} catch (ResponseEntityException e) { 
			return e.buildResponse();
		} catch (ClassCastException | NullPointerException e) {
			return new InvalidValueException().buildResponse();
		} catch (IllegalArgumentException e) {
			return new InvalidFieldnameException().buildResponse();
		}
	}
	
	@ApiOperation(
			value = "Delete a manager.",
			notes = "Delete a manager user in database."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message="Manager deleted."),
		@ApiResponse(code = 401, message="Invalid authentification token."),
		@ApiResponse(code = 403, message="User isn't an administrator."),
		@ApiResponse(code = 404, message="Manager not found."),
		@ApiResponse(code = 409, message="Manager linked to one or more consultants.")
	})
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/{managerId}")
	public ResponseEntity<?> deleteUser(
			@ApiParam("Manager's identifier.")
			@PathVariable Long managerId) {
		try {
			Manager manager = dal.findById(managerId)
				.orElseThrow(() -> new ResourceNotFoundException(Manager.class));
			if (!manager.getConsultants().isEmpty())
				throw new EntityDeletionException("The manager is linked to one or more consultants.");
			dal.delete(manager);
			return ResponseEntity.ok(null);
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	}
	
	private class ManagerResponseBodyBuilder{
		Manager manager;
		Map <String, Object> body = new HashMap<String, Object>();
		
		ManagerResponseBodyBuilder(Manager manager){
			this.manager = manager;
		}
		
		ManagerResponseBodyBuilder id() {
			body.put("id", manager.getId());
			return this;
		}
		
		ManagerResponseBodyBuilder firstname() {
			body.put("firstname", manager.getFirstname());
			return this;
		}
		
		ManagerResponseBodyBuilder lastname() {
			body.put("lastname", manager.getLastname());
			return this;
		}
		
		ManagerResponseBodyBuilder email() {
			body.put("email", manager.getEmail());
			return this;
		}

		ManagerResponseBodyBuilder admin() {
			body.put("admin", manager.isAdmin());
			return this;
		}
		
		ManagerResponseBodyBuilder releaseDate() {
			body.put("releaseDate", manager.getReleaseDate());
			return this;
		}
		
		ManagerResponseBodyBuilder consultants() {
			body.put("consultants", manager.getConsultants().stream()
					.map(consultant -> {
						Map <String, Object> map = new HashMap<String, Object>();
						map.put("id", consultant.getId());
						map.put("firstname", consultant.getFirstname());
						map.put("lastname", consultant.getLastname());
						map.put("email", consultant.getEmail());
						return map;
					})
					.collect(Collectors.toList()));
			return this;
		}
		
		Map <String, Object> build() {
			return body;
		}
	}
}