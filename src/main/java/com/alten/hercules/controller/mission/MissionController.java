package com.alten.hercules.controller.mission;

import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.dal.MissionDAL;
import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.mission.EState;
import com.alten.hercules.model.mission.EType;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.request.MissionFastRequest;
import com.alten.hercules.model.mission.request.MissionRequest;
import com.alten.hercules.model.response.MsgResponse;

@RestController
@RequestMapping("/hercules/missions")
public class MissionController {

	@Autowired
	private MissionDAO missionDAO;

	@Autowired
	private MissionDAL missionDAL;

	@GetMapping
	public List<Mission> getAll() {
		return this.missionDAO.findAll();
	}

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
		 

		this.missionDAL.fastInsert(cons, cust);

		return new ResponseEntity(HttpStatus.CREATED);
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

		if (this.missionDAO.lastVersionByReference(reference) == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new MsgResponse("No mission found with the reference."));

		Mission mission = this.missionDAO.lastVersionByReference(reference);

		Date now = new Date();
		long diffTime = getDateDiff(now, mission.getLastUpdate(), TimeUnit.HOURS);
		System.out.println(diffTime);

		if (diffTime < 24) {
			// if last update is less than 24h then update last version

			setMissionParameters(mission, req);

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
			this.missionDAO.save(mission);
		} else {
			// last update is more than 24h => new version is created
			System.out.println("new version");
			Mission newMission = new Mission(mission);
			setMissionParameters(newMission, req);

			if (req.getConsultantId() != null) {
				if (this.missionDAL.existsConsultant(req.getConsultantId()))
					newMission.setConsultant(this.missionDAL.getConsultant(req.getConsultantId()));
				else
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MsgResponse(
							"Update new mission: no consultant found with id=" + req.getConsultantId()));
			}

			if (req.getCustomerId() != null) {
				if (this.missionDAL.existsCustomer(req.getCustomerId()))
					newMission.setCustomer(this.missionDAL.getCustomer(req.getCustomerId()));
				else
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
							new MsgResponse("Update new mission: no customer found with id=" + req.getCustomerId()));
			}

			newMission.setLastUpdate(now);
			this.missionDAO.save(newMission);

		}
		return ResponseEntity.ok().build();
	}

	private static Mission setMissionParameters(Mission mission, MissionRequest req) {
		if (req.getTitle() != null && !req.getTitle().isEmpty())
			mission.setTitle(req.getTitle());

		if (req.getDescription() != null && !req.getDescription().isEmpty())
			mission.setDescription(req.getDescription());

		if (req.getType() != null)
			mission.setType(req.getType());

		if (req.getCity() != null && !req.getCity().isEmpty())
			mission.setCity(req.getCity());

		if (req.getCountry() != null && !req.getCountry().isEmpty())
			mission.setCountry(req.getCountry());

		if (req.getComment() != null && !req.getComment().isEmpty())
			mission.setComment(req.getComment());

		if (req.getConsultantRole() != null && !req.getConsultantRole().isEmpty())
			mission.setConsultantRole(req.getConsultantRole());

		if (req.getConsultantExperience() != null)
			mission.setConsultantExperience(req.getConsultantExperience());

		if (req.getState() != null)
			mission.setState(req.getState());

		if (req.getTeamSize() != null)
			mission.setTeamSize(req.getTeamSize());

		return mission;
	}

	private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date1.getTime() - date2.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

}
