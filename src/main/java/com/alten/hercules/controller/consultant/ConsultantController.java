package com.alten.hercules.controller.consultant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;
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

import com.alten.hercules.controller.consultant.http.request.AddConsultantRequest;
import com.alten.hercules.controller.http.request.UpdateEntityRequest;
import com.alten.hercules.dal.ConsultantDAL;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.consultant.EConsultantFieldname;
import com.alten.hercules.model.exception.EntityDeletionException;
import com.alten.hercules.model.exception.InvalidFieldnameException;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.exception.ResponseEntityException;
import com.alten.hercules.model.exception.RessourceNotFoundException;
import com.alten.hercules.model.exception.UnavailableEmailException;
import com.alten.hercules.model.user.Manager;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/hercules/consultants")
public class ConsultantController {

	@Autowired
	private ConsultantDAL dal;

	@GetMapping("")
	public ResponseEntity<?> getAll(@RequestParam boolean enabled) {
		return enabled ?
				ResponseEntity.ok(dal.findAllEnabled()) :
				ResponseEntity.ok(dal.findAll());
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping("/rgpd")
	public ResponseEntity<?> setConsultantRgpd() {
		for(Consultant c : this.dal.findReleasedConsultantFiveyears()) {
			String fname = c.getFirstname();
			String lname = c.getLastname();
			try {
				String f1 = fname.substring(0, 1);
				String l1 = lname.substring(0, 1);
				c.setFirstname(f1);
				c.setLastname(l1);
				this.dal.save(c);
			} catch (InvalidValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		try {
			Consultant consultant = dal.findById(id)
					.orElseThrow(() -> new RessourceNotFoundException("consultant"));
			return ResponseEntity.ok(consultant);
		} catch (RessourceNotFoundException e) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(e.getMessage());
		}
	}

	@PreAuthorize("hasAuthority('MANAGER')")
	@PostMapping("")
	public ResponseEntity<?> addConsultant(@Valid @RequestBody AddConsultantRequest req) {
		Optional<Consultant> optConsultant = dal.findByEmail(req.getEmail());
		if (optConsultant.isPresent())
			return ResponseEntity
					.accepted()
					.body(optConsultant.get().getId());
		try {
			if (!dal.emailIsAvailable(req.getEmail()))
				throw new UnavailableEmailException();
			Manager manager = dal.findEnabledManager(req.getManager())
					.orElseThrow(() -> new RessourceNotFoundException("manager"));
			Consultant consultant = new Consultant(req.getEmail(), req.getFirstname(), req.getLastname(), manager);
			dal.save(consultant);
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(consultant.getId());
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	}

	@PreAuthorize("hasAuthority('MANAGER')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteConsultant(@PathVariable Long id) {
		try {
			Consultant consultant = dal.findById(id)
				.orElseThrow(() -> new RessourceNotFoundException("consultant"));
			if (!consultant.getMissions().isEmpty())
				throw new EntityDeletionException("The consultant is linked to one or more missions.");
			dal.delete(consultant);
			return ResponseEntity
					.ok()
					.build();
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	}

	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping
	public ResponseEntity<?> updateConsultant(@Valid @RequestBody UpdateEntityRequest req) { 
		try {
			Consultant consultant = dal.findById(req.getId())
					.orElseThrow(() -> new RessourceNotFoundException("consultant"));
			EConsultantFieldname fieldName;
			try { fieldName = EConsultantFieldname.valueOf(req.getFieldName()); }
			catch (IllegalArgumentException e) { throw new InvalidFieldnameException(); }
			switch(fieldName) {
				case firstname :
					consultant.setFirstname((String)req.getValue());
					break;
				case lastname :
					consultant.setLastname((String)req.getValue());
					break;
				case email :
					String email = (String)req.getValue();
					if (!dal.emailIsAvailable(email))
						throw new UnavailableEmailException();
					consultant.setEmail(email);
					break;
				case experience:
					consultant.setExperience((Integer)req.getValue());
					break;
				case manager :
					if(req.getValue() instanceof Integer || req.getValue() instanceof Long) {
						int id = (Integer)req.getValue();
						Manager manager = dal.findEnabledManager(Long.valueOf(id))
							.orElseThrow(() -> new RessourceNotFoundException("manager"));
						consultant.setManager(manager);
					}
					else
						throw new InvalidValueException();
					break;
				case releaseDate:
					try {
						consultant.setReleaseDate(new SimpleDateFormat("yyyy-MM-dd").parse((String)req.getValue()));
					} catch (ParseException e) {
						throw new InvalidValueException();
					}
					break;
				default: throw new InvalidFieldnameException();
			}
			dal.save(consultant);
			return ResponseEntity.ok().build();
		} catch (ResponseEntityException e) { 
			return e.buildResponse();
		} catch (ClassCastException | NullPointerException e) {
			return new InvalidValueException().buildResponse();
		}
	}
}