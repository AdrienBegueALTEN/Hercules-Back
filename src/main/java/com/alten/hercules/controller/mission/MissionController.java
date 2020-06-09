package com.alten.hercules.controller.mission;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import com.alten.hercules.controller.http.request.UpdateEntityRequest;
import com.alten.hercules.controller.mission.http.request.AddMissionRequest;
import com.alten.hercules.controller.mission.http.request.GeneratePDFRequest;
import com.alten.hercules.controller.mission.http.response.RefinedMissionResponse;
import com.alten.hercules.controller.mission.http.response.CompleteMissionResponse;
import com.alten.hercules.dal.MissionDAL;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.exception.AlreadyExistingVersionException;
import com.alten.hercules.model.exception.EntityDeletionException;
import com.alten.hercules.model.exception.InvalidFieldnameException;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.exception.NotLastVersionException;
import com.alten.hercules.model.exception.ProjectsBoundsException;
import com.alten.hercules.model.exception.ResponseEntityException;
import com.alten.hercules.model.exception.ResourceNotFoundException;
import com.alten.hercules.model.exception.InvalidSheetStatusException;
import com.alten.hercules.model.mission.EContractType;
import com.alten.hercules.model.mission.EMissionFieldname;
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;
import com.alten.hercules.model.project.EProjectFieldname;
import com.alten.hercules.model.project.Project;
import com.alten.hercules.model.skill.Skill;
import com.alten.hercules.service.PDFGenerator;
import com.alten.hercules.service.StoreImage;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/missions")
public class MissionController {

	@Autowired private MissionDAL dal;
	@Autowired private StoreImage storeImage;
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getMission(@PathVariable Long id) {
		return getMissionDetails(id, true);
	}
	
	@PreAuthorize("hasAuthority('MISSION')")
	@GetMapping("/anonymous")
	public ResponseEntity<?> getMissionFromToken() {
		Long id = ((Mission)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getId();
		return getMissionDetails(id, false);
	}
	
	private ResponseEntity<?> getMissionDetails(Long id, boolean complete) {
		try {
			Mission mission = dal.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Mission"));
			return ResponseEntity.ok(complete ? 
					new CompleteMissionResponse(mission, true, true) :
					new RefinedMissionResponse(mission));
		} catch (ResourceNotFoundException e) {
			return e.buildResponse();
		}
	}
	
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteMission(@PathVariable Long id) {
		try {
			Mission mission = dal.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Mission"));
			if (!(mission.getLastVersion().getVersionDate()==null)&&!(mission.getSheetStatus().equals(ESheetStatus.ON_WAITING)))
				throw new EntityDeletionException("The mission is not on waiting and has a last version date");
			dal.delete(mission);
			return ResponseEntity
					.ok()
					.build();
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	}
	
	
	@GetMapping("")
	public ResponseEntity<?> getAll(@RequestParam Optional<Long> manager) {
		List<CompleteMissionResponse> body;
		if (manager.isEmpty()) {
			body = dal.findAllValidated().stream()
					.map(mission -> new CompleteMissionResponse(mission, false, false))
					.collect(Collectors.toList());
		} else {
			body = dal.findAllByManager(manager.get()).stream()
					.map(mission -> new CompleteMissionResponse(mission, false, true))
					.collect(Collectors.toList());
		}
		return ResponseEntity.ok(body);
	}


	@PreAuthorize("hasAuthority('MANAGER')")
	@PostMapping
	public ResponseEntity<?> addMission(@Valid @RequestBody AddMissionRequest request) {
		try {
			Consultant consultant = dal.findConsultantById(request.getConsultant())
					.orElseThrow(() -> new ResourceNotFoundException("Consultant"));
			Customer customer = dal.findCustomerById(request.getCustomer())
					.orElseThrow(() -> new ResourceNotFoundException("Customer"));
			Mission mission = dal.save(new Mission(consultant, customer));
			MissionSheet firstVersion = dal.saveSheet(new MissionSheet(mission));
			dal.saveProject(new Project(firstVersion));
			return ResponseEntity.status(HttpStatus.CREATED).body(mission.getId());
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@GetMapping("/new-version/{missionId}")
	public ResponseEntity<?> newVersion(@PathVariable Long missionId) {
		try {
			Mission mission = dal.findById(missionId)
					.orElseThrow(() -> new ResourceNotFoundException("Mission"));
				if (!mission.isValidated())
					throw new InvalidSheetStatusException();
				MissionSheet lastVersion = dal.findMostRecentVersion(mission.getId()).get();;
				if (isToday(lastVersion.getVersionDate()))
					throw new AlreadyExistingVersionException();
				MissionSheet newVersion = dal.saveSheet(new MissionSheet(lastVersion));
				newVersion.getProjects().forEach(project -> dal.saveProject(project));
				mission.changeSecret();
				mission.setSheetStatus(ESheetStatus.ON_WAITING);
				dal.save(mission);
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping
	public ResponseEntity<?> putMission(@Valid @RequestBody UpdateEntityRequest req) {
		return updateMission(req.getId(), req.getFieldName(), req.getValue());
	}
	
	@PreAuthorize("hasAuthority('MISSION')")
	@PutMapping("/anonymous")
	public ResponseEntity<?> putMissionFromToken(@RequestBody UpdateEntityRequest req) {
		Long id = ((Mission)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getId();
		if (req.getFieldName() == null)
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.build();
		return updateMission(id, req.getFieldName(), req.getValue());
	}
	
	private ResponseEntity<?> updateMission(Long id, String key, Object value) {
		try {
			MissionSheet mostRecentVersion = dal.findMostRecentVersion(id)
					.orElseThrow(() -> new ResourceNotFoundException("Sheet"));
			Mission mission = dal.findById(id).get();
			
			EMissionFieldname fieldname;
			try { fieldname = EMissionFieldname.valueOf(key); }
			catch (IllegalArgumentException e) { throw new InvalidFieldnameException(); }
			switch(fieldname) {
				case city :
					mostRecentVersion.setCity((String)value);
					break;
				case comment :
					mostRecentVersion.setComment((String)value);
					break;
				case consultantRole :
					mostRecentVersion.setConsultantRole((String)value);
					break;
				case consultantStartXp :
					mostRecentVersion.setConsultantStartXp((Integer)value);
					break;
				case contractType :
					try { 
						EContractType contractType = EContractType.valueOf((String)value);
						mostRecentVersion.setContractType(contractType);
					} catch (IllegalArgumentException e) { throw new InvalidValueException(); }
					break;
				case country :
					mostRecentVersion.setCountry((String)value);
					break;
				case description :
					mostRecentVersion.setDescription((String)value);
					break;
				case sheetStatus :
					try { 
						ESheetStatus status = ESheetStatus.valueOf((String)value);
						if (!status.equals(ESheetStatus.VALIDATED))
							throw new InvalidValueException();
						if (mission.getSheetStatus().equals(ESheetStatus.VALIDATED))
							throw new InvalidSheetStatusException();
						mission.changeSecret();
						mission.setSheetStatus(ESheetStatus.VALIDATED);
						mostRecentVersion.setVersionDate(new Date());
						dal.save(mission);
					} catch (IllegalArgumentException e) { throw new InvalidValueException(); }
					return ResponseEntity.ok().build(); 
				case teamSize :
					mostRecentVersion.setTeamSize((Integer)value);
					break;
				case title :
					mostRecentVersion.setTitle((String)value);
					break;
				default: throw new InvalidFieldnameException();
			}
			updateSheetStatus(mission);
			dal.saveSheet(mostRecentVersion);
			return ResponseEntity.ok().build();
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		} catch (ClassCastException | NullPointerException e) {
			return new InvalidValueException().buildResponse();
		}
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@GetMapping("/new-project/{missionId}")
	public ResponseEntity<?> NewProject(@PathVariable Long missionId) {
		try { newProject(missionId); }
		catch (ResponseEntityException e) {
			return e.buildResponse();
		}
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PreAuthorize("hasAuthority('MISSION')")
	@GetMapping("/new-project-anonymous")
	public ResponseEntity<?> newProjectFromToken() {
		Long missionId = ((Mission)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getId();
		try { newProject(missionId); }
		catch (ResponseEntityException e) {
			return e.buildResponse();
		}
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	private void newProject(Long missionId) throws ResponseEntityException {
		Mission mission = dal.findById(missionId)
				.orElseThrow(() -> new ResourceNotFoundException("Mission"));
			if (mission.isValidated())
				throw new InvalidSheetStatusException();
			MissionSheet lastVersion = mission.getLastVersion();
			if (!(lastVersion.getProjects().size() < 5))
				throw new ProjectsBoundsException();
			Project newProject = new Project(lastVersion);
			dal.addProjectForSheet(lastVersion, newProject);
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping("projects")
	public ResponseEntity<?> putProject(@Valid @RequestBody UpdateEntityRequest req) {
		return updateProject(req.getId(), req.getFieldName(), req.getValue());
	}
	
	@PreAuthorize("hasAuthority('MISSION')")
	@PutMapping("projects/anonymous")
	public ResponseEntity<?> putProjectFromToken(@Valid @RequestBody UpdateEntityRequest req) {
		Long missionId = ((Mission)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getId();
		try {
			Project project = dal.findProjectById(req.getId())
					.orElseThrow(() -> new ResourceNotFoundException("Project"));
			if (project.getMissionSheet().getMission().getId() != missionId)
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (ResourceNotFoundException e) {
			return e.buildResponse();
		}
		return updateProject(req.getId(), req.getFieldName(), req.getValue());
	}
	
	private ResponseEntity<?> updateProject(Long id, String key, Object value){
		try {
			Project project = dal.findProjectById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Project"));
			if (project.getMissionSheet().getMission().isValidated())
				throw new InvalidSheetStatusException();
			checkIfProjectOfLastVersion(project);
			EProjectFieldname fieldName;
			try { fieldName = EProjectFieldname.valueOf(key); }
			catch (IllegalArgumentException e) { throw new InvalidFieldnameException(); }
			switch(fieldName) {
			case title:
				project.setTitle((String)value);
				break;
			case description:
				project.setDescription((String)value);
				break;
			case beginDate:
				project.setBeginDate(new SimpleDateFormat("yyyy-MM-dd").parse((String)value));
				break;
			case endDate:
				project.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse((String)value));
				break;
			default: 
				throw new InvalidFieldnameException();
			}
			updateSheetStatus(project.getMissionSheet().getMission());
			dal.saveProject(project);
			return ResponseEntity.ok(null);
		} catch (ResponseEntityException e) { 
			return e.buildResponse();
		} catch (ClassCastException | NullPointerException | ParseException e) {
			return new InvalidValueException().buildResponse();
		}
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@DeleteMapping("projects/{projectId}")
	public ResponseEntity<?> deleteProject(@PathVariable Long projectId) {
		try { projectDeletion(projectId); }
		catch (ResponseEntityException e) {
			return e.buildResponse();
		}
		return ResponseEntity.ok(null);
	}
	
	@PreAuthorize("hasAuthority('MISSION')")
	@DeleteMapping("projects/anonymous/{projectId}")
	public ResponseEntity<?> deleteProjectFromToken(@PathVariable Long projectId) {
		try {
			Long missionId = ((Mission)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getId();
			Project project = dal.findProjectById(projectId)
					.orElseThrow(() -> new ResourceNotFoundException("Project"));
			if (project.getMissionSheet().getMission().getId() != missionId)
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			projectDeletion(projectId);
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
		return ResponseEntity.ok(null);
	}
	
	private void projectDeletion(Long id) throws ResponseEntityException {
			Project project = dal.findProjectById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Project"));
			if (project.getMissionSheet().getMission().isValidated())
				throw new InvalidSheetStatusException();
			if (!(project.getMissionSheet().getMission().getLastVersion().getProjects().size() > 1))
				throw new ProjectsBoundsException();
			checkIfProjectOfLastVersion(project);
			removeSkillFromDeletionProject(project);
			this.storeImage.delete(StoreImage.PROJECT_FOLDER+project.getPicture());
			dal.removeProject(project);
	}
	
	private void removeSkillFromDeletionProject(Project project) {
		Set<Skill> skillsCopy = new HashSet<>();
		for(Skill skill : project.getSkills()) {
			skillsCopy.add(skill);
		}
		for(Skill skill : skillsCopy) {
			this.dal.removeSkillFromProject(project, skill);
		}
	}
	
	private void checkIfProjectOfLastVersion(Project project) throws NotLastVersionException {
		if (project.getMissionSheet().getMission().getLastVersion().getId() != project.getMissionSheet().getId())
			throw new NotLastVersionException();
	}
	
	private void updateSheetStatus(Mission mission) {
		if (mission.getSheetStatus().equals(ESheetStatus.ON_WAITING)) {
			mission.setSheetStatus(ESheetStatus.ON_GOING);
			dal.save(mission);
		}
	}
	
	private boolean isToday(Date date) {
		Calendar todayCalendar = Calendar.getInstance();
		Calendar lastVersionCalendar = Calendar.getInstance();
		lastVersionCalendar.setTime(date);
		
		return todayCalendar.get(Calendar.DAY_OF_YEAR) == lastVersionCalendar.get(Calendar.DAY_OF_YEAR) 
				&& todayCalendar.get(Calendar.YEAR) == lastVersionCalendar.get(Calendar.YEAR);
	}
	
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@PostMapping("/projects/{id}/upload-picture")
	public ResponseEntity<?> uploadLogoManager(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
		return this.uploadPicture(file, id);
	}
	
	@PreAuthorize("hasAuthority('MISSION')")
	@PostMapping("/projects/anonymous/{id}/upload-picture")
	public ResponseEntity<?> uploadLogoToken(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
		return this.uploadPicture(file, id);
	}
	
	private ResponseEntity<?> uploadPicture(MultipartFile file, Long id){
		try {
			Project proj = this.dal.findProjectById(id).orElseThrow(() -> new ResourceNotFoundException("Project"));
			if(proj.getPicture()!=null) {
				this.storeImage.delete("img/proj/"+proj.getPicture());
				proj.setPicture(null);
			}
			storeImage.save(file,"project");
			proj.setPicture(file.getOriginalFilename());
			this.dal.saveProject(proj);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@DeleteMapping("/projects/{id}/delete-picture")
	public ResponseEntity<?> deletePictureManager(@PathVariable Long id) {
		return this.deletePicture(id);
	}
	
	@PreAuthorize("hasAuthority('MISSION')")
	@DeleteMapping("/projects/anonymous/{id}/delete-picture")
	public ResponseEntity<?> deletePictureToken(@PathVariable Long id) {
		Long missionId = ((Mission)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getId();
		try {
			Project project = dal.findProjectById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Project"));
			if (project.getMissionSheet().getMission().getId() != missionId)
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (ResourceNotFoundException e) {
			return e.buildResponse();
		}
		return this.deletePicture(id);
	}
	
	private ResponseEntity<?> deletePicture(Long projectId){
		try {
			Project proj = this.dal.findProjectById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project"));
			if(proj.getPicture()!=null) {
				this.storeImage.delete("img/proj/"+proj.getPicture());
				proj.setPicture(null);
			}
			this.dal.saveProject(proj);
		} catch (ResourceNotFoundException e) {
			// TODO Auto-generated catch block
			return e.buildResponse();
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping("/projects/picture/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = storeImage.loadFileAsResource(fileName,"project");
        
        if(resource == null) {
        	return ResponseEntity.notFound().build();
        }
        
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
        	System.err.println(ex);
        }
        
        if(contentType == null) {
        	contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@PostMapping("/projects/{id}/skills")
	public ResponseEntity<?> addSkillToProjectManager(@PathVariable Long id, @RequestBody String... labels) {
		return this.addSkillToProject(id, labels);
	}
	
	@PreAuthorize("hasAuthority('MISSION')")
	@PostMapping("/projects/anonymous/{id}/skills")
	public ResponseEntity<?> addSkillToProjectToken(@PathVariable Long id, @RequestBody String... labels) {
		Long missionId = ((Mission)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getId();
		try {
			Project project = dal.findProjectById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Project"));
			if (project.getMissionSheet().getMission().getId() != missionId)
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (ResourceNotFoundException e) {
			return e.buildResponse();
		}
		return this.addSkillToProject(id, labels);
	}
	
	private ResponseEntity<?> addSkillToProject(Long id, String... labels) {
		try {
			Project proj = this.dal.findProjectById(id).orElseThrow(() -> new ResourceNotFoundException("Project"));
			for(int i=0;i<labels.length;i++) {
				Skill s = this.dal.findSkillByLabel(labels[i]).orElse(new Skill(labels[i]));
				this.dal.addSkillToProject(proj, s);
			}
			return ResponseEntity.ok().build();
		} catch (ResourceNotFoundException e) {
			return e.buildResponse();
		}
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@DeleteMapping("/projects/{id}/skills")
	public ResponseEntity<?> removeSkillFromProjectManager(@PathVariable Long id, @RequestBody Skill skill) {
		return this.removeSkillFromProject(id, skill);
	}
	
	@PreAuthorize("hasAuthority('MISSION')")
	@DeleteMapping("/projects/anonymous/{id}/skills")
	public ResponseEntity<?> removeSkillFromProjectToken(@PathVariable Long id, @RequestBody Skill skill) {
		Long missionId = ((Mission)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getId();
		try {
			Project project = dal.findProjectById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Project"));
			if (project.getMissionSheet().getMission().getId() != missionId)
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (ResourceNotFoundException e) {
			return e.buildResponse();
		}
		return this.removeSkillFromProject(id, skill);
	}
	
	private ResponseEntity<?> removeSkillFromProject(Long id, Skill skill) {
		try {
			Project proj = this.dal.findProjectById(id).orElseThrow(() -> new ResourceNotFoundException("Project"));
			Skill s = this.dal.findSkillByLabel(skill.getLabel())
					.orElseThrow(() -> new ResourceNotFoundException("Project"));
			this.dal.removeSkillFromProject(proj, s);
			return ResponseEntity.ok().build();
		} catch (ResourceNotFoundException e) {
			return e.buildResponse();
		}
	}
	
	@GetMapping("/projects/skills-all")
	public ResponseEntity<?> getAllSkills() {
		return ResponseEntity.ok(this.dal.findAllSkills());
	}
	
	
	@PostMapping("/pdf")
	public ResponseEntity<?> generatePDF(@Valid @RequestBody List<GeneratePDFRequest> elements ) {
		
		
		int n = elements.size();
		
		for(int i = 0; i<n ; i++) {
			try {
				
					if(elements.get(i).getType().equals("m")){
						Mission mission = dal.findById(elements.get(i).getId())
								.orElseThrow(() -> new ResourceNotFoundException("Mission"));
						PDFGenerator.makeMissionPDF(mission);
					
					}
					else {
						Project project = dal.findProjectById(elements.get(i).getId())
								.orElseThrow(() -> new ResourceNotFoundException("Project"));
						PDFGenerator.makeProjectPDF(project);
					}
				
			} catch (ResourceNotFoundException e) {
				return e.buildResponse();
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("file not found");
			} 
		}
		
			
		
		return ResponseEntity.ok("");
	}
	
	
	
	
}
