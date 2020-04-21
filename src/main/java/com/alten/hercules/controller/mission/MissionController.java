package com.alten.hercules.controller.mission;

import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.dal.MissionDAL;
import com.alten.hercules.dao.mission.MissionDAO;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.request.MissionFastRequest;

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
	
	/*@PutMapping
	public ResponseEntity<?> updateMission(@RequestBody MissionRequest req){
		if(!this.missionDAO.findById(req.getId()).isPresent())
			return new ResponseEntity(HttpStatus.CONFLICT);
		
		Mission mission = this.missionDAO.findById(req.getId()).get();
		
		if(mission.getLastUpdate())
		
		String title = (req.getTitle()==null)?null:req.getTitle();
		String description = (req.getDescription()==null)?null:req.getDescription();
		EType type = (req.getType()==null)?null:req.getType();
		String city = (req.getCity()==null)?null:req.getCity();
		String country = (req.getCountry()==null)?null:req.getCountry();
		String comment = (req.getComment()==null)?null:req.getComment();
		String consultantRole  = (req.getConsultantRole()==null)?null:req.getConsultantRole();
		int consultantExperience = (req.getConsultantExperience()==null)?0:req.getConsultantExperience();
		EState state  = (req.getState()==null)?null:req.getState();
		int teamSize =(req.getTeamSize()==null)?0:req.getTeamSize();
		
		
		
		return ResponseEntity.ok().build();
	}*/
	
	
}
