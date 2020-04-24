package com.alten.hercules.controller.mission;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.dal.MissionDAL;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.request.MissionFastRequest;
import com.alten.hercules.model.mission.request.UpdateMissionRequest;
import com.alten.hercules.model.response.MsgResponse;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/missions")
public class MissionController {

	@Autowired
	private MissionDAL dal;

	@GetMapping
	public List<Mission> getAll() {
		return this.dal.findAll();
	}
	
	@GetMapping("/last-versions")
	public List<Mission> getAllUniqueVersion() {
		return this.dal.allMissionLastUpdate();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> getById(@PathVariable Long id){
		if(this.dal.findById(id)!=null)
			return new ResponseEntity<>(this.dal.findById(id),HttpStatus.OK);
		return ResponseEntity.notFound().build();
	}
	
	/**
	 * 
	 * @param reference
	 * @return
	 */
	@GetMapping("/reference/{reference}")
	public ResponseEntity<Object> getLastMossionOfReference(@PathVariable Long reference){
		if(this.dal.byReference(reference)==null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MsgResponse("No mission with reference="+reference));
		return new ResponseEntity<>(this.dal.byReference(reference),HttpStatus.OK);
	}
	
	//@PreAuthorize("hasAuthority('ADMIN ')")
	@PostMapping
	public ResponseEntity<Object> fastInsertion(@Valid @RequestBody MissionFastRequest req) {
		Consultant consultant = dal.findConsultantById(req.getConsultantId()).get();
		if (consultant != null)
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error : consultant not found");
		
		Customer customer = dal.findCustomerById(req.getCustomerId()).get();
		if (customer != null)
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error : customer not found");

		Mission mission = new Mission(consultant, customer);
		dal.save(mission);
		mission.setReference(mission.getId());
		dal.save(mission);

		return ResponseEntity.status(HttpStatus.CREATED).body(mission.getId());
	}

	/**
	 * Update the mission.<br>
	 * The mission id in the request is mandatory. Other parameters are not
	 * obligatory.<br>
	 * If a modification appears 24h after the previous one, then the version is
	 * incremented.
	 * 
	 * @param request
	 * @return NOT FOUND OK once it is updated
	 */
	@PutMapping
	public ResponseEntity<Object> updateMission(@Valid @RequestBody UpdateMissionRequest request) {
		Optional<Mission> otpMission = dal.lastVersionByReference(request.getReference());

		if (!otpMission.isPresent())
			 return ResponseEntity.notFound().build();
		
		Mission mission = otpMission.get();
		Date now = new Date();

		if (moreThan24hDifference(now, mission.getLastUpdate()))
			mission = new Mission(mission);

		Mission.setMissionParameters(mission, request);
		mission.setLastUpdate(now);
		this.dal.save(mission);
		
		return ResponseEntity.ok(mission.getId());
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteMission(@PathVariable Long id){
		Optional<Mission> otpMission = dal.findById(id);
		
		if (!otpMission.isPresent())
			 return ResponseEntity.notFound().build();
		
		Mission mission = otpMission.get();
		/*TODO
		if (nombre de projets != 0)
			return ResponseEntity.status(HttpStatus.CONFLICT).build()*/
		dal.delete(mission);
		return ResponseEntity.ok().build();
	}

	private static boolean moreThan24hDifference(Date date1, Date date2) {
		long diffInMillies = Math.abs(date1.getTime() - date2.getTime());
		return TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS) > 24;
	}

}
