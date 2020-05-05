package com.alten.hercules.controller.mission;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.alten.hercules.controller.http.request.UpdateEntityRequest;
import com.alten.hercules.controller.mission.http.request.AddMissionRequest;
import com.alten.hercules.controller.mission.http.response.MissionDetailsResponse;
import com.alten.hercules.dal.MissionDAL;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.exception.AlreadyExistingVersionException;
import com.alten.hercules.model.exception.InvalidFieldNameException;
import com.alten.hercules.model.exception.InvalidRessourceFormatException;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.exception.RessourceNotFoundException;
import com.alten.hercules.model.exception.UnvalidatedMissionSheetException;
import com.alten.hercules.model.mission.EMissionFieldname;
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/missions")
public class MissionController {

	@Autowired private MissionDAL dal;
	
	@GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable Long id) {
		Optional<Mission> optMission = dal.findById(id);
		if (optMission.isEmpty())
			return ResponseEntity.notFound().build();
		
		return ResponseEntity.ok(new MissionDetailsResponse(optMission.get()));
	}
	
	
	@GetMapping("")
	public List<MissionDetailsResponse> getAll() {
		
		List<MissionDetailsResponse> missions = new ArrayList<>();
		
		dal.findAll().forEach((mission) -> {
			missions.add(new MissionDetailsResponse(mission)); });
		
		return missions;
					
		
	}

	
	@PreAuthorize("hasAuthority('MANAGER')")
	@PostMapping
	public ResponseEntity<?> addMission(@Valid @RequestBody AddMissionRequest req) {
		Optional<Consultant> optConsultant = dal.findConsultantById(req.getConsultant());
		if (optConsultant.isEmpty())
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consultant");
		
		Optional<Customer> optCustomer = dal.findCustomerById(req.getCustomer());
		if (optCustomer.isEmpty())
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer");

		Mission mission = new Mission(optConsultant.get(), optCustomer.get());
		MissionSheet v0 = new MissionSheet(mission);
		dal.save(mission);
		dal.saveSheet(v0);

		return ResponseEntity.status(HttpStatus.CREATED).body(mission.getId());
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@PostMapping("/new-version/{id}")
	public ResponseEntity<?> newVersion(@PathVariable Long id) {
		try {
			Mission mission = dal.findById(id)
					.orElseThrow(() -> new RessourceNotFoundException("mission"));
			
			if (!mission.getSheetStatus().equals(ESheetStatus.VALIDATED))
				throw new UnvalidatedMissionSheetException();
			
			MissionSheet mostRecentVersion = dal.findMostRecentVersion(id)
					.orElseThrow(() -> new RessourceNotFoundException("mission sheet"));
			
			if (isToday(mostRecentVersion.getVersionDate()))
				throw new AlreadyExistingVersionException();
			
			dal.saveSheet(new MissionSheet(mostRecentVersion, new Date()));
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (RessourceNotFoundException e) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(e.getMessage());
		} catch (AlreadyExistingVersionException e) {
			return ResponseEntity
					.status(HttpStatus.CONFLICT)
					.body(e.getMessage());
		} catch (UnvalidatedMissionSheetException e) {
			return ResponseEntity
					.badRequest()
					.body(e.getMessage());
		}
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping
	public ResponseEntity<?> updateMission(@Valid @RequestBody UpdateEntityRequest req) { 
		try {
			MissionSheet mostRecentVersion = dal.findMostRecentVersion(req.getId())
					.orElseThrow(() -> new RessourceNotFoundException("mission sheet"));
			
			EMissionFieldname fieldname;
			try { fieldname = EMissionFieldname.valueOf(req.getFieldName()); }
			catch (IllegalArgumentException e) { throw new InvalidFieldNameException(); }
			switch(fieldname) {
				case city :
					mostRecentVersion.setCity((String)req.getValue());
					break;
				case comment :
					mostRecentVersion.setComment((String)req.getValue());
					break;
				case consultantStartXp :
					mostRecentVersion.setConsultantStartXp((Integer)req.getValue());
					break;
				case country :
					mostRecentVersion.setCountry((String)req.getValue());
					break;
				case description :
					mostRecentVersion.setDescription((String)req.getValue());
					break;
				case sheetStatus :

					break;
				case teamSize :
					mostRecentVersion.setTeamSize((Integer)req.getValue());
					break;
				case title :
					mostRecentVersion.setTitle((String)req.getValue());
					break;
				default: throw new InvalidFieldNameException();
			}
			dal.saveSheet(mostRecentVersion);
			return ResponseEntity.ok().build();
		} catch (InvalidFieldNameException | InvalidValueException | InvalidRessourceFormatException e) { 
			return ResponseEntity
					.badRequest()
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
	
	private boolean isToday(Date date) {
		Calendar todayCalendar = Calendar.getInstance();
		Calendar lastVersionCalendar = Calendar.getInstance();
		lastVersionCalendar.setTime(date);
		
		return todayCalendar.get(Calendar.DAY_OF_YEAR) == lastVersionCalendar.get(Calendar.DAY_OF_YEAR) 
				&& todayCalendar.get(Calendar.YEAR) == lastVersionCalendar.get(Calendar.YEAR);
	}
}
