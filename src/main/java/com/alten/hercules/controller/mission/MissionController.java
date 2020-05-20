package com.alten.hercules.controller.mission;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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
import com.alten.hercules.service.StoreImage;
import com.alten.hercules.utils.EmlFileUtils;
import com.alten.hercules.model.exception.ResourceNotFoundException;



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
	
	@GetMapping("/from-token")
	public ResponseEntity<?> getMissionFromToken() {
		Long id = (Long)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
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
				.orElseThrow(() -> new ResourceNotFoundException("mission"));
			if (!(mission.getSheetStatus().equals(ESheetStatus.ON_WAITING)))
				throw new EntityDeletionException("The mission is not on waiting");
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
	public ResponseEntity<?> addMission(@Valid @RequestBody AddMissionRequest req) {
		try {
			Consultant consultant = dal.findConsultantById(req.getConsultant())
					.orElseThrow(() -> new ResourceNotFoundException("Consultant"));
			Customer customer = dal.findCustomerById(req.getCustomer())
					.orElseThrow(() -> new ResourceNotFoundException("Customer"));
			Mission mission = new Mission(consultant, customer);
			MissionSheet v0 = new MissionSheet(mission);
			dal.save(mission);
			dal.saveSheet(v0);
			return ResponseEntity.status(HttpStatus.CREATED).body(mission.getId());
		} catch (ResourceNotFoundException e) {
			return e.buildResponse();
		}
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@GetMapping("/new-version/{id}")
	public ResponseEntity<?> newVersion(@PathVariable Long id) {
		try {
			Mission mission = dal.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Mission"));
			if (!mission.isValidated())
				throw new InvalidSheetStatusException();
			MissionSheet lastVersion = dal.findMostRecentVersion(id)
					.orElseThrow(() -> new ResourceNotFoundException("Sheet"));
			if (isToday(lastVersion.getVersionDate()))
				throw new AlreadyExistingVersionException();
			MissionSheet newVersion = dal.saveSheet(new MissionSheet(lastVersion, new Date()));
			newVersion.getProjects().forEach(project -> dal.saveProject(project));
			mission.changeSecret();
			mission.setSheetStatus(ESheetStatus.ON_WAITING);
			dal.save(mission);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping
	public ResponseEntity<?> putMission(@Valid @RequestBody UpdateEntityRequest req) {
		return updateMission(req.getId(), req.getFieldName(), req.getValue());
	}
	
	@PreAuthorize("hasAuthority('ANONYMOUS')")
	@PutMapping("/from-token")
	public ResponseEntity<?> putMissionFromToken(@RequestBody UpdateEntityRequest req) {
		Long id = (Long)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
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
						mission.setSheetStatus(status);
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
	@GetMapping("email-access/{id}")
	public ResponseEntity<?> getAnonymousTokenForMission(@PathVariable Long id) {
		File file = null;
		ResponseEntity<?> response = null;
		try {
			Mission mission = dal.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Mission"));
			if (mission.getSheetStatus().equals(ESheetStatus.VALIDATED))
				throw new InvalidSheetStatusException();
			file = EmlFileUtils.genereateEmlFile(mission).orElseThrow();
			response = buildEmlFileResponse(file);
		} catch (ResponseEntityException e) {
			response = e.buildResponse();
		} catch (IOException e) {
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (file != null) file.delete();
		}
		return response;		
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@GetMapping("/new-project/{missionId}")
	public ResponseEntity<?> getNewProject(@PathVariable Long missionId) {
		return newProject(missionId);
	}
	
	@PreAuthorize("hasAuthority('ANONYMOUS')")
	@GetMapping("/new-project-from-token")
	public ResponseEntity<?> getNewProjectFromToken() {
		Long missionId = (Long)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		return newProject(missionId);
	}
	
	private ResponseEntity<?> newProject(Long missionId) {
		try {
			Mission mission = dal.findById(missionId)
					.orElseThrow(() -> new ResourceNotFoundException("Mission"));
			if (mission.isValidated())
				throw new InvalidSheetStatusException();
			MissionSheet lastVersion = mission.getLastVersion();
			if (!(lastVersion.getProjects().size() < 5))
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			Project newProject = new Project(lastVersion);
			dal.addProjectForSheet(lastVersion, newProject);
			return ResponseEntity.ok(null);
		} catch (ResponseEntityException e) {
			return e.buildResponse();
		}
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@PutMapping("projects")
	public ResponseEntity<?> putProject(@Valid @RequestBody UpdateEntityRequest req) {
		return updateProject(req.getId(), req.getFieldName(), req.getValue());
	}
	
	@PreAuthorize("hasAuthority('ANONYMOUS')")
	@PutMapping("projects/from-token")
	public ResponseEntity<?> putProjectFromToken(@Valid @RequestBody UpdateEntityRequest req) {
		Long missionId = (Long)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
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
	@DeleteMapping("projects/{id}")
	public ResponseEntity<?> deleteProject(@PathVariable Long id) {
		return projectDeletion(id);
	}
	
	@PreAuthorize("hasAuthority('ANONYMOUS')")
	@DeleteMapping("projects/from-token/{projectId}")
	public ResponseEntity<?> deleteProjectFromToken(@PathVariable Long projectId) {
		Long missionId = (Long)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		try {
			Project project = dal.findProjectById(projectId)
					.orElseThrow(() -> new ResourceNotFoundException("Project"));
			if (project.getMissionSheet().getMission().getId() != missionId)
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (ResourceNotFoundException e) {
			return e.buildResponse();
		}
		return projectDeletion(projectId);
	}
	
	private ResponseEntity<?> projectDeletion(Long id) {
		try {
			Project project = dal.findProjectById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Project"));
			if (project.getMissionSheet().getMission().isValidated())
				throw new InvalidSheetStatusException();
			checkIfProjectOfLastVersion(project);
			this.storeImage.delete(StoreImage.PROJECT_FOLDER+project.getPicture());
			dal.removeProject(project);
			return ResponseEntity.ok().build();
		} catch (ResponseEntityException e) {
			return e.buildResponse();
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
	
	private ResponseEntity<ByteArrayResource> buildEmlFileResponse(File file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
		Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
	    return ResponseEntity.ok()
	            .headers(headers)
	            .contentLength(file.length())
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .body(resource);
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
		return this.uploadLogo(file, id);
	}
	
	private ResponseEntity<?> uploadLogo(MultipartFile file, Long id){
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
}
