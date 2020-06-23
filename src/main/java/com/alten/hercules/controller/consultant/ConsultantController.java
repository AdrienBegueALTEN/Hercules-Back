package com.alten.hercules.controller.consultant;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.user.AppUser;
import com.alten.hercules.model.user.Manager;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Class that manages the requests sent to the API for the consultants.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/hercules/consultants")
public class ConsultantController {

	@Autowired
	private ConsultantDAL dal;
	
	/**
	 * Returns a list of all or only enabled consultants.
	 * @param active Boolean asking for enabled consultants
	 * @return 200 with the consultants list
	 */
	@ApiOperation(value="List of all consultants.", notes = "Get a list of all consultants or only those still active.")
	@ApiResponses({
		@ApiResponse(code = 200, message="OK."),
		@ApiResponse(code = 401, message="Invalid authentificated token.")
	})
	@GetMapping("")
	public ResponseEntity<?> getAll(
			@ApiParam("Indicates if only active consultants should be returned.\n"
					+ "False if isn't specified.")
			@RequestParam Optional<Boolean> active) {
		return active.orElse(false) ?
				ResponseEntity.ok(dal.findAllEnabled()) :
				ResponseEntity.ok(dal.findAll());
	}

	/**
	 * Returns a consultant object corresponding to the given ID.
	 * @param consultantId  Number of the consultant
	 * @return 200 with a consultant object<br>404 when none is found
	 */
	@ApiOperation(value="Details of a consultant.", notes = "Get the details of a consultant by giving his ID number.")
	@ApiResponses({
		@ApiResponse(code = 200, message="OK."),
		@ApiResponse(code = 404, message="Consultant not found."),
		@ApiResponse(code = 401, message="Invalid authentificated token.")
	})
	@GetMapping("/{consultantId}")
	public ResponseEntity<?> getById(
			@ApiParam("Consultant's identifier.")
			@PathVariable Long consultantId) {
		try {
			Consultant consultant = dal.findById(consultantId)
					.orElseThrow(() -> new ResourceNotFoundException(Consultant.class));
			return ResponseEntity.ok(new ConsultantResponse(consultant));
		} catch (ResourceNotFoundException e) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(e.getMessage());
		}
	}

	/**
	 * Create a new consultant. Checks first if the manager of the new consultant or if a consultant with same email address exists.
	 * @param request  object that provides the new consultant informations.
	 * @return 201 if the consultant is added<br>404 if the manager is not found<br>202 if the consultant already exists with the given email
	 */
	@ApiOperation(value="Creation of a consultant.", notes = "Create a new consultant by providing his identity, his email address and his manager's ID. ")
	@ApiResponses({
		@ApiResponse(code = 201, message="Consultant is created."),
		@ApiResponse(code = 202, message="Consultant already exists."),
		@ApiResponse(code = 401, message="Invalid authentificated token or user isn't manager."),
		@ApiResponse(code = 404, message="Manager not found.")
	})
	@PreAuthorize("hasAuthority('MANAGER')")
	@PostMapping
	public ResponseEntity<?> addConsultant(
			@ApiParam(
					"email : consultant's email;\n"
					+ "firstname : consultant's firstname;\n"
					+ "lastname : consultant's lastname;\n"
					+ "manager : identifier of the manager in charge of the consultant."
				)
			@Valid @RequestBody AddConsultantRequest request) {
		Optional<Consultant> optConsultant = dal.findByEmail(request.getEmail());
		if (optConsultant.isPresent())
			return ResponseEntity
					.accepted()
					.body(optConsultant.get().getId());
		try {
			if (!dal.emailIsAvailable(request.getEmail()))
				throw new UnavailableEmailException();
			Manager manager = dal.findEnabledManager(request.getManager())
					.orElseThrow(() -> new ResourceNotFoundException(Manager.class));
			Consultant consultant = new Consultant(request.getEmail(), request.getFirstname(), request.getLastname(), manager);
			dal.save(consultant);
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(consultant.getId());
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	}

	/**
	 * Delete a consultant. It checks if the consultant's ID can be found and if the consultant is not linked to any missions.
	 * @param consultantId  the consultant's id
	 * @return 404 if the id doesn't correspond to any consultant<br>200 if the deletion is done<br>409 consultant linked to 1 or more missions
	 */
	@ApiOperation(value="Deletion of a consultant.", notes = "Delete a consultant if he exists given his id, and if he is not linked to a mission.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Consultant is deleted."),
		@ApiResponse(code = 401, message="Invalid authentificated token or user isn't manager."),
		@ApiResponse(code = 404, message="Consultant not found."),
		@ApiResponse(code = 409, message="Consultant is linked to 1 or more missions.")
	})
	@PreAuthorize("hasAuthority('MANAGER')")
	@DeleteMapping("/{consultantId}")
	public ResponseEntity<?> deleteConsultant(
			@ApiParam("Consultant's identifier.")
			@PathVariable Long consultantId) {
		try {
			Consultant consultant = dal.findById(consultantId)
				.orElseThrow(() -> new ResourceNotFoundException(Consultant.class));
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

	/**
	 * Update a single field of a consultant given the id of the consultant, the field name to be updated and the new value.
	 * @param req  object containing the id of the consultant, the field name to update, and the new value.
	 * @return 404 if the consultant cannot be found<br>400 if the field name cannot be found or the value is of wrong type<br>200 if update is done
	 */
	@ApiOperation(value="Update a field of a consultant.", 
			notes = "Update a field of the consultant corresponding to the ID in the request if he exists.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Consultant updated."),
		@ApiResponse(code = 401, message="Field name is not found or value is of wrong type."),
		@ApiResponse(code = 401, message="Invalid authentificated token or user isn't manager."),
		@ApiResponse(code = 404, message="Consultant not found.")
	})
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping
	public ResponseEntity<?> updateConsultant(
			@ApiParam(
					"id : consultant's identifier;\n"
					+ "fieldname : field's name to update;\n"
					+ "value : field's new value."
			)
			@Valid @RequestBody UpdateEntityRequest req) { 
		try {
			Consultant consultant = dal.findById(req.getId())
					.orElseThrow(() -> new ResourceNotFoundException(Consultant.class));
			EConsultantFieldname fieldName = EConsultantFieldname.valueOf(req.getFieldName());
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
					Manager manager = dal.findEnabledManager(((Integer)req.getValue()).longValue())
						.orElseThrow(() -> new ResourceNotFoundException(Manager.class));
					consultant.setManager(manager);
					break;
				case releaseDate:
					LocalDate releaseDate = req.getValue() != null ? LocalDate.parse((String)req.getValue()) : null;
					consultant.setReleaseDate(releaseDate);
					break;
				default: throw new InvalidFieldnameException();
			}
			dal.save(consultant);
			return ResponseEntity.ok().build();
		} catch (ResponseEntityException e) { 
			return e.buildResponse();
		} catch (ClassCastException | NullPointerException e) {
			return new InvalidValueException().buildResponse();
		} catch (IllegalArgumentException e) {
			return new InvalidFieldnameException().buildResponse();
		}
	}
	
	/**
	 * Create then add a new diploma to a consultant if he exists. The request object provides the consultant's id and the diploma informations.
	 * @param request object provides the consultant's id and the diploma informations
	 * @return 404 if the consultant is not found<br>200 if the diploma is created and added to the consultant
	 */
	@ApiOperation(value="Create a new diploma of a consultant.", notes = "Create and add a new diploma object to the consultant. "
			+ "The request provides the consultant's ID and the diploma details.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Diploma added."),
		@ApiResponse(code = 401, message="Invalid authentificated token or user isn't manager."),
		@ApiResponse(code = 404, message="Consultant not found.")
	})
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping("add-diploma")
	public ResponseEntity<?> addDiploma(
			@ApiParam(
				"consultant : identifier of diploma's owner;\n"
				+ "establishment : name of the establishment that awarded the diploma;\n"
				+ "entitled : diploma's entitled;\n"
				+ "level : diploma's level;\n"
				+ "year : year of obtention of the diplome"
			)
			@Valid @RequestBody AddDiplomaRequest request) {
		try {
			Consultant consultant = dal.findById(request.getConsultant())
					.orElseThrow(() -> new ResourceNotFoundException(Consultant.class));
			Diploma diploma = dal.addDiplomaForConsultant(request.buildDiploma(), consultant);
			return ResponseEntity.ok(diploma.getId());
		} catch (ResourceNotFoundException e) { return e.buildResponse(); }
	}
	
	/**
	 * Update a single field of diploma, given the diploma id, the filed to be updated and the new value.
	 * @param request object containing the id of the diploma, the field name to update, and the new value
	 * @return  404 if the diploma cannot be found<br>400 if the field name cannot be found or the value is of wrong type<br>200 if update is done
	 */
	@ApiOperation(value="Update a field of a diploma.", 
			notes = "Update a field of the diploma corresponding to the diploma ID in the request if it exists.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Diploma updated."),
		@ApiResponse(code = 401, message="Field name is not found or value is of wrong type."),
		@ApiResponse(code = 401, message="Invalid authentificated token or user isn't manager."),
		@ApiResponse(code = 404, message="Diploma not found.")
	})
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping("update-diploma")
	public ResponseEntity<?> updateDiploma(
			@ApiParam(
					"id : diploma's identifier;\n"
					+ "fieldname : field's name to update;\n"
					+ "value : field's new value."
			)
			@Valid @RequestBody UpdateEntityRequest request) {
		try {
			if (request.getValue() == null)
				throw new InvalidValueException();
			Diploma diploma = dal.findDiplomaById(request.getId())
					.orElseThrow(() -> new ResourceNotFoundException(Diploma.class));
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
	
	/**
	 * Deletes from the database a diploma associated to a consultant. It also removes it from the consultant list of diploma.
	 * @param request  the request object (consultant's ID and diploma ID)
	 * @return 200 if the diploma is deleted<br>404 if the consultant or diploma are not found
	 */
	@ApiOperation(value="Deletion of a diploma.", notes = "Delete a diploma and remove it from a consultant. "
			+ "The request provides the consultant's ID and the diploma ID.")
	@ApiResponses({
		@ApiResponse(code = 200, message="The diploma is removed from the consultant."),
		@ApiResponse(code = 401, message="Invalid authentificated token or user isn't administrator."),
		@ApiResponse(code = 404, message="Consultant or diploma not found.")
	})
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping("remove-diploma")
	public ResponseEntity<?> removeDiploma(
			@ApiParam(
				"consultant : identifier of diploma's owner;\n"
				+ "diploma : diploma's identifier."
			)
			@Valid @RequestBody RemoveDiplomaRequest request){
		try {
			Diploma diploma = dal.findDiplomaById(request.getDiploma())
					.orElseThrow(() -> new ResourceNotFoundException(Diploma.class));
			Consultant consultant = dal.findById(request.getConsultant())
					.orElseThrow(() -> new ResourceNotFoundException(Consultant.class));
			dal.removeDiplomaForConsultant(diploma, consultant);
			return ResponseEntity.ok(null);
		} catch (ResourceNotFoundException e) { return e.buildResponse(); }
	}
	
	/**
	 * Returns the list of the missions associated to a consultant.
	 * @param missionId  the ID of the mission
	 * @return 200 with the list of the missions of a consultant.
	 */
	@ApiOperation(value = "List of the missions of a consultant.", notes = "Get all missions of a consultant given the consultant's ID. "
			+ "If the connected user is not a manager ,then it returns only the validated missions.")
	@ApiResponses({
		@ApiResponse(code = 200, message="OK."),
		@ApiResponse(code = 401, message="Invalid authentificated token.")
	})
	@GetMapping("/{missionId}/missions")
	public ResponseEntity<?> getConsultantMissions(
			@ApiParam("Mission's identifier.")
			@PathVariable Long missionId) {
		AppUser user = ((AppUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
		Optional<Long> optManagerId = Optional.ofNullable(user instanceof Manager ? user.getId() : null);
		if(optManagerId.isPresent())
			return ResponseEntity.ok(this.dal.findMissionsByConsultant(missionId).stream()
					.map(mission -> new CompleteMissionResponse(mission, false, true))
					.collect(Collectors.toList()));
		else
			return ResponseEntity.ok(this.dal.findMissionsByConsultant(missionId).stream()
					.filter(mission -> mission.getSheetStatus().equals(ESheetStatus.VALIDATED))
					.map(mission -> new CompleteMissionResponse(mission, false, false))
					.collect(Collectors.toList()));
	}
}