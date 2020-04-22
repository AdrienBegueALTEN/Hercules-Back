package com.alten.hercules.controller.mission;

import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.dal.MissionDAL;
import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.request.MissionFastRequest;
import com.alten.hercules.model.mission.request.MissionRequest;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/missions")
public class MissionController {
	
	@Autowired
	private MissionDAO missionDAO;
	
	@Autowired
	private MissionDAL missionDAL;
	
	@GetMapping
	public List<Mission> getAll(){
		return this.missionDAO.findAll();
	}
	
	@PostMapping
	public ResponseEntity<?> fastInsertion(@Valid @RequestBody MissionFastRequest req){
		
		Long cons = req.getConsultantId();
		Long cust = req.getCustomerId();
		
		if(cons==null || cust==null) 
			return ResponseEntity.noContent().build();
		
		/*if(!this.missionDAL.existsConsultant(cons) || !this.missionDAL.existsCustomer(cust))
			return ResponseEntity.notFound().build();*/
		
		this.missionDAL.fastInsert(cons, cust);
		
		return new ResponseEntity(HttpStatus.CREATED);
	}
	
	/**
	 * Update the mission.<br>
	 * The mission id in the request is mandatory. Other parameters are not obligatory.<br> 
	 * If a modification appears 24h after the previous one, then the version is incremented.
	 * 
	 * @param req
	 * @return
	 * NOT FOUND
	 * OK once it is updated
	 */
	@PutMapping
	public ResponseEntity<?> updateMission(@RequestBody MissionRequest req){
		if(!this.missionDAO.findById(req.getId()).isPresent())
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		
		Date now = new Date();
		
		Mission mission = this.missionDAO.findById(req.getId()).get();
		
		if(getDateDiff(now, mission.getLastUpdate(), TimeUnit.HOURS)>=24)
			mission.setVersion(mission.getVersion()+1);
		
		if(req.getTitle()!=null && !req.getTitle().isEmpty()) 
			mission.setTitle(req.getTitle());
		
		if(req.getDescription()!=null && !req.getDescription().isEmpty()) 
			mission.setDescription(req.getDescription());
		
		if(req.getType()!=null) 
			mission.setType(req.getType());		
		
		if(req.getCity()!=null && !req.getCity().isEmpty()) 
			mission.setCity(req.getCity());		
		
		if(req.getCountry()!=null && !req.getCountry().isEmpty()) 
			mission.setCountry(req.getCountry());		
		
		if(req.getComment()!=null && !req.getComment().isEmpty()) 
			mission.setComment(req.getComment());		
		
		if(req.getConsultantRole()!=null && !req.getConsultantRole().isEmpty()) 
			mission.setConsultantRole(req.getConsultantRole());		
		
		if(req.getConsultantExperience()!=null) 
			mission.setConsultantExperience(req.getConsultantExperience());
		
		if(req.getState()!=null)
			mission.setState(req.getState());
		
		if(req.getTeamSize()!=null) 
			mission.setTeamSize(req.getTeamSize());
		
		if(req.getConsultantId()!=null && this.missionDAL.existsConsultant(req.getConsultantId()))
			mission.setConsultant(this.missionDAL.getConsultant(req.getConsultantId()));
		else
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		
		if(req.getCustomerId()!=null && this.missionDAL.existsCustomer(req.getCustomerId()))
			mission.setCustomer(this.missionDAL.getCustomer(req.getCustomerId()));
		else
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		
		mission.setLastUpdate(now);
		this.missionDAO.save(mission);
		return ResponseEntity.ok().build();
	}
	
	private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	
}
