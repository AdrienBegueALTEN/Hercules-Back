package com.alten.hercules.controller.mission;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.request.MissionFastRequest;
import com.alten.hercules.model.mission.request.MissionRequest;
import com.alten.hercules.model.response.MsgResponse;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/missions")
public class MissionController {

	@Autowired
	private MissionDAL missionDAL;

	@GetMapping
	public List<Mission> getAll() {
		return this.missionDAL.findAll();
	}
	
	@GetMapping("/last-versions")
	public List<Mission> getAllUniqueVersion() {
		return this.missionDAL.allMissionLastUpdate();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id){
		if(this.missionDAL.findById(id)!=null)
			return new ResponseEntity<>(this.missionDAL.findById(id),HttpStatus.OK);
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	
	/**
	 * 
	 * @param reference
	 * @return
	 */
	@GetMapping("/reference/{reference}")
	public ResponseEntity<?> getLastMossionOfReference(@PathVariable Long reference){
		if(this.missionDAL.byReference(reference)==null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MsgResponse("No mission with reference="+reference));
		return new ResponseEntity<>(this.missionDAL.byReference(reference),HttpStatus.OK);
	}
	
	//@PreAuthorize("hasAuthority('ADMIN ')")
	@PostMapping
	public ResponseEntity<?> fastInsertion(@RequestBody MissionFastRequest req) {

		Long cons = req.getConsultantId();
		Long cust = req.getCustomerId();

		if (cons == null || cust == null)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new MsgResponse("Consultant or customer is null."));

		
		if(!this.missionDAL.existsConsultant(cons))	
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MsgResponse("Consultant ("+cons+")not found."));	
		if(!this.missionDAL.existsCustomer(cust)) 
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MsgResponse("Customer ("+cust+") not found."));
		 

		Mission created = this.missionDAL.fastInsert(cons, cust);

		return ResponseEntity.status(HttpStatus.CREATED).body(created.getId());
	}

	/**
	 * Update the mission.<br>
	 * The mission id in the request is mandatory. Other parameters are not
	 * obligatory.<br>
	 * If a modification appears 24h after the previous one, then the version is
	 * incremented.
	 * 
	 * @param req
	 * @return NOT FOUND OK once it is updated
	 */
	@PutMapping
	public ResponseEntity<?> updateMission(@RequestBody MissionRequest req) {
		Long reference = req.getReference();
		if (reference == null)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new MsgResponse("No reference in request."));

		if (this.missionDAL.lastVersionByReference(reference) == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new MsgResponse("No mission found with the reference."));

		Mission mission = this.missionDAL.lastVersionByReference(reference);

		Date now = new Date();
		long diffTime = getDateDiff(now, mission.getLastUpdate(), TimeUnit.HOURS);
		System.out.println(diffTime);

		if (diffTime < 24) {
			// if last update is less than 24h then update last version

			Mission.setMissionParameters(mission, req);

			if (req.getConsultantId() != null) {
				if (this.missionDAL.existsConsultant(req.getConsultantId()))
					mission.setConsultant(this.missionDAL.getConsultant(req.getConsultantId()));
				else
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MsgResponse(
							"Update current mission: no consultant found with id=" + req.getConsultantId()));
			}

			if (req.getCustomerId() != null) {
				if (this.missionDAL.existsCustomer(req.getCustomerId()))
					mission.setCustomer(this.missionDAL.getCustomer(req.getCustomerId()));
				else
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MsgResponse(
							"Update current mission: no customer found with id=" + req.getCustomerId()));
			}

			mission.setLastUpdate(now);
			this.missionDAL.save(mission);
		} else {
			// last update is more than 24h => new version is created
			System.out.println("new version");
			Mission newMission = new Mission(mission);
			Mission.setMissionParameters(newMission, req);

			if (req.getConsultantId() != null) {
				if (this.missionDAL.existsConsultant(req.getConsultantId()))
					newMission.setConsultant(this.missionDAL.getConsultant(req.getConsultantId()));
				else
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MsgResponse(
							"Update new version: no consultant found with id=" + req.getConsultantId()));
			}

			if (req.getCustomerId() != null) {
				if (this.missionDAL.existsCustomer(req.getCustomerId()))
					newMission.setCustomer(this.missionDAL.getCustomer(req.getCustomerId()));
				else
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
							new MsgResponse("Update new version: no customer found with id=" + req.getCustomerId()));
			}

			newMission.setLastUpdate(now);
			this.missionDAL.save(newMission);

		}
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping
	public ResponseEntity<?> deleteMission(@RequestBody Mission mission){
		if(this.missionDAL.findById(mission.getId())==null) 
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new MsgResponse("Mission id="+mission.getId()+" not found"));
		
		//TODO nb projets==0
		this.missionDAL.delete(mission);
		
		return ResponseEntity.ok().build();
	}
	
	

	

	private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date1.getTime() - date2.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

}
