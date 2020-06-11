package com.alten.hercules.controller.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
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

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/managers")
public class ManagerController {
	
	@Autowired private ManagerDAL dal;
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getManager(@PathVariable Long id) {
		try {
			Manager manager = dal.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("manager"));
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
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	 }
	
	@GetMapping
	public ResponseEntity<Object> getAll() {
		return ResponseEntity.ok(dal.findAll());
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping
	public ResponseEntity<Object> addManager(@Valid @RequestBody AddManagerRequest request) {
		if (!dal.emailIsAvailable(request.getEmail()))
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		Manager manager = request.buildUser();
		manager = dal.save(manager);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(manager.getId());
	}
	 
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping
	public ResponseEntity<?> updateManager(@Valid @RequestBody UpdateEntityRequest request) {
		try {
			Manager manager = dal.findById(request.getId())
					.orElseThrow(() -> new ResourceNotFoundException("manager"));
			EManagerFieldName fieldName;
			try { fieldName = EManagerFieldName.valueOf(request.getFieldName()); }
			catch (IllegalArgumentException e) { throw new InvalidFieldnameException(); }
			switch(fieldName) {
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
					try {
						manager.setReleaseDate(new SimpleDateFormat("yyyy-MM-dd").parse((String)request.getValue()));
					} catch (ParseException e) {
						throw new InvalidValueException();
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
		}
	}
	 
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		try {
			Manager manager = dal.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("recruitment officer"));
			if (!manager.getConsultants().isEmpty())
				throw new EntityDeletionException("The manager is linked to one or more consultants.");
			else
				dal.delete(manager);
			return ResponseEntity.ok(manager);
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