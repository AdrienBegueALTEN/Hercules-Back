package com.alten.hercules.controller.consultant;

import java.util.Date;
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
import com.alten.hercules.controller.consultant.http.request.UpdateConsultantRequest;
import com.alten.hercules.dal.ConsultantDAL;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.consultant.EConsultantFieldName;
import com.alten.hercules.model.exception.InvalidRessourceFormatException;
import com.alten.hercules.model.exception.InvalidFieldNameException;
import com.alten.hercules.model.exception.InvalidValueException;
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
	public ResponseEntity<Object> getAll(@RequestParam boolean enabled) {
		return enabled ?
				ResponseEntity.ok(dal.findAllEnabled()) :
				ResponseEntity.ok(dal.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getById(@PathVariable Long id) {
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
	public ResponseEntity<Object> addConsultant(@Valid @RequestBody AddConsultantRequest req) {
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
		} catch (UnavailableEmailException e) {
			return ResponseEntity
					.status(HttpStatus.CONFLICT)
					.body(e.getMessage());
		} catch (RessourceNotFoundException e) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(e.getMessage());
		}
	}

	@PreAuthorize("hasAuthority('MANAGER')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteConsultant(@PathVariable Long id) {
		try {
			Consultant consultant = dal.findById(id)
				.orElseThrow(() -> new RessourceNotFoundException("consultant"));
			if (!consultant.getMissions().isEmpty())
				return ResponseEntity
						.status(HttpStatus.CONFLICT)
						.build();
			dal.delete(consultant);
			return ResponseEntity
					.ok()
					.build();
		} catch (RessourceNotFoundException e) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(e.getMessage());
		}
	}

	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping
	public ResponseEntity<?> updateConsultant(@Valid @RequestBody UpdateConsultantRequest req) {
		try {
			Consultant consultant = dal.findById(req.getId())
					.orElseThrow(() -> new RessourceNotFoundException("consultant"));
			EConsultantFieldName fieldName;
			try { fieldName = EConsultantFieldName.valueOf(req.getFieldName()); }
			catch (IllegalArgumentException e) { throw new InvalidFieldNameException(); }
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
				case xp:
					consultant.setExperience((Integer)req.getValue());
					break;
				case manager :
					int id = (Integer)req.getValue();
					Manager manager = dal.findEnabledManager(Long.valueOf(id))
						.orElseThrow(() -> new RessourceNotFoundException("manager"));
					consultant.setManager(manager);
					break;
				case releaseDate:
					consultant.setReleaseDate((Date)req.getValue());
					break;
				default: throw new InvalidFieldNameException();
			}
			dal.save(consultant);
			return ResponseEntity.ok().build();
		} catch (InvalidFieldNameException | InvalidValueException | InvalidRessourceFormatException e) { 
			return ResponseEntity
					.badRequest()
					.body(e.getMessage());
		} catch (UnavailableEmailException e) {
			return ResponseEntity
					.status(HttpStatus.CONFLICT)
					.body(e.getMessage());
		} catch (RessourceNotFoundException e) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(e.getMessage());
		} catch (ClassCastException e) {
			return ResponseEntity
					.badRequest()
					.body("Invalid value type");
		}
	}
}