package com.alten.hercules.controller.consultant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import com.alten.hercules.controller.consultant.http.request.AddConsultantRequest;
import com.alten.hercules.controller.consultant.http.request.AddDiplomaRequest;
import com.alten.hercules.controller.consultant.http.request.RemoveDiplomaRequest;
import com.alten.hercules.controller.consultant.http.response.ConsultantResponse;
import com.alten.hercules.controller.http.request.UpdateEntityRequest;
import com.alten.hercules.controller.mission.http.response.CompleteMissionResponse;
import com.alten.hercules.dal.ConsultantDAL;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.consultant.EConsultantFieldname;
import com.alten.hercules.model.diploma.Diploma;
import com.alten.hercules.model.diploma.EDiplomaFieldname;
import com.alten.hercules.model.exception.EntityDeletionException;
import com.alten.hercules.model.exception.InvalidFieldnameException;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.exception.ResponseEntityException;
import com.alten.hercules.model.exception.ResourceNotFoundException;
import com.alten.hercules.model.exception.UnavailableEmailException;
import com.alten.hercules.model.user.Manager;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/hercules/consultants")
public class ConsultantController {

	@Autowired
	private ConsultantDAL dal;
	
	@ApiOperation(value = "Récupère tous les consultants.")
	@GetMapping("")
	public ResponseEntity<?> getAll(@RequestParam boolean enabled) {
		return enabled ?
				ResponseEntity.ok(dal.findAllEnabled()) :
				ResponseEntity.ok(dal.findAll());
	}

	@ApiOperation(value = "Récupère un consultant avec son id.")
	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		try {
			Consultant consultant = dal.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("consultant"));
			return ResponseEntity.ok(new ConsultantResponse(consultant));
		} catch (ResourceNotFoundException e) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(e.getMessage());
		}
	}

	@ApiOperation(value = "Ajoute un nouveau consultant.")
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
					.orElseThrow(() -> new ResourceNotFoundException("manager"));
			Consultant consultant = new Consultant(req.getEmail(), req.getFirstname(), req.getLastname(), manager);
			dal.save(consultant);
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(consultant.getId());
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	}

	@ApiOperation(value = "Supprime un consultant avec son id.")
	@PreAuthorize("hasAuthority('MANAGER')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteConsultant(@PathVariable Long id) {
		try {
			Consultant consultant = dal.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("consultant"));
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

	@ApiOperation(value = "Met à jour un champ d'un consultant.")
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping
	public ResponseEntity<?> updateConsultant(@Valid @RequestBody UpdateEntityRequest req) { 
		try {
			Consultant consultant = dal.findById(req.getId())
					.orElseThrow(() -> new ResourceNotFoundException("consultant"));
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
							.orElseThrow(() -> new ResourceNotFoundException("manager"));
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
	
	@ApiOperation(value = "Ajoute un diplôme à un consultant.")
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping("add-diploma")
	public ResponseEntity<?> addDiploma(@Valid @RequestBody AddDiplomaRequest request) {
		try {
			Consultant consultant = dal.findById(request.getConsultant())
					.orElseThrow(() -> new ResourceNotFoundException("Consultant"));
			Diploma diploma = dal.addDiplomaForConsultant(request.buildDiploma(), consultant);
			return ResponseEntity.ok(diploma.getId());
		} catch (ResourceNotFoundException e) { return e.buildResponse(); }
	}
	
	@ApiOperation(value = "Met à jour un champ d'un diplôme.")
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping("update-diploma")
	public ResponseEntity<?> updateDiploma(@Valid @RequestBody UpdateEntityRequest request) {
		try {
			if (request.getValue() == null)
				throw new InvalidValueException();
			Diploma diploma = dal.findDiplomaById(request.getId())
					.orElseThrow(() -> new ResourceNotFoundException("Diploma"));
			EDiplomaFieldname fieldName;
			try { fieldName = EDiplomaFieldname.valueOf(request.getFieldName()); }
			catch (IllegalArgumentException e) { throw new InvalidFieldnameException(); }
			switch (fieldName) {
			case entitled:
				diploma.setEntitled((String)request.getValue());
				break;
			case establishment:
				diploma.setEstablishment((String)request.getValue());
				break;
			case level:
				diploma.setLevel((String)request.getValue());
				break;
			case year:
				diploma.setYear((Integer)request.getValue());
				break;
			default: throw new InvalidFieldnameException();
			}
			dal.saveDiploma(diploma);
			return ResponseEntity.ok(null);
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		} catch (ClassCastException e) {
			return new InvalidValueException().buildResponse();
		}
	}
	
	@ApiOperation(value = "Supprime un diplome d'un consultant.")
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping("remove-diploma")
	public ResponseEntity<?> removeDiploma(@Valid @RequestBody RemoveDiplomaRequest request){
		try {
			Diploma diploma = dal.findDiplomaById(request.getDiploma())
					.orElseThrow(() -> new ResourceNotFoundException("Diploma"));
			Consultant consultant = dal.findById(request.getConsultant())
					.orElseThrow(() -> new ResourceNotFoundException("Consultant"));
			dal.removeDiplomaForConsultant(diploma, consultant);
			return ResponseEntity.ok(null);
		} catch (ResourceNotFoundException e) { return e.buildResponse(); }
	}
	
	@ApiOperation(value = "Récupère toutes les missions faites par un consultant.")
	@GetMapping("/{id}/missions")
	public ResponseEntity<?> getConsultantMissions(@PathVariable Long id){
		return ResponseEntity.ok(this.dal.findMissionsByConsultant(id).stream()
				.map(mission -> new CompleteMissionResponse(mission, false, true))
				.collect(Collectors.toList()));
	}
}