package com.alten.hercules.controller.auth;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.controller.auth.http.request.ChangePasswordRequest;
import com.alten.hercules.controller.auth.http.request.LoginRequest;
import com.alten.hercules.dal.AuthenticationDAL;
import com.alten.hercules.model.exception.InvalidSheetStatusException;
import com.alten.hercules.model.exception.ResourceNotFoundException;
import com.alten.hercules.model.exception.ResponseEntityException;
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.response.JWTResponse;
import com.alten.hercules.model.user.AppUser;
import com.alten.hercules.utils.EmlFileUtils;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/auth")
public class AuthController {
	
	@Autowired private AuthenticationManager authManager;
	@Autowired private AuthenticationDAL  dal;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {
		Authentication authentication = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return ResponseEntity.ok(new JWTResponse((AppUser)authentication.getPrincipal()));
	}
	
	@PreAuthorize("hasAuthority('CHANGE_PASSWORD')")
	@GetMapping("/change-password-anonymous")
	public ResponseEntity<?> checkTokenValidity() {
		return ResponseEntity.ok(null);
	}
	
	@PreAuthorize("hasAuthority('CHANGE_PASSWORD')")
	@PutMapping("/change-password-anonymous")
	public ResponseEntity<?> changePasswordAnonymous(@RequestBody String password) {
		AppUser user = (AppUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (user.getPassword() != null)
			ResponseEntity.status(HttpStatus.CONFLICT).body("Password already definded.");
		user = dal.findUserById(user.getId()).get();
		user.setPassword(password);
		user = dal.saveUser(user);
		return ResponseEntity.ok(new JWTResponse(user));
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECRUITMENT_OFFICER')")
	@PutMapping("/change-password")
	public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
		AppUser user = (AppUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		String providedCurrentPassword = request.getCurrentPassword();
		if (!new BCryptPasswordEncoder().matches(providedCurrentPassword, user.getPassword()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The provided password doesn't match with the current password.");
		user.setPassword(request.getNewPassword());
		user = dal.saveUser(user);
		return ResponseEntity.ok(null);
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@GetMapping("mission-sheet-access/{missionId}")
	public ResponseEntity<?> missionSheetExternalAccess(@PathVariable Long missionId) {
		File file = null;
		ResponseEntity<?> response = null;
		try {
			Mission mission = dal.findMissionById(missionId)
					.orElseThrow(() -> new ResourceNotFoundException("Mission"));
			AppUser loggedUser = (AppUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
			if (mission.getSheetStatus().equals(ESheetStatus.VALIDATED))
				throw new InvalidSheetStatusException();
			if (loggedUser.equals(mission.getConsultant().getManager())) {
				file = EmlFileUtils.genereateEmlFileWithMissionLink(mission).orElseThrow();
				response = buildEmlFileResponse(file);
			} else response = ResponseEntity
					.status(HttpStatus.FORBIDDEN)
					.body("Only the manager of the consultant linkied to this mission can do this action.");
		} catch (ResponseEntityException e) {
			response = e.buildResponse();
		} catch (IOException e) {
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (file != null) file.delete();
		}
		return response;		
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("password-creation-access/{userId}")
	public ResponseEntity<?> getAnonymousTokenForMission(@PathVariable Long userId) {
		File file = null;
		ResponseEntity<?> response = null;
		try {
			AppUser user = dal.findUserById(userId)
					.orElseThrow(() -> new ResourceNotFoundException("User"));
			file = EmlFileUtils.genereateEmlFileWithPasswordCreationLink(user).orElseThrow();
			response = buildEmlFileResponse(file);
			user.expireCredentials();;
			dal.saveUser(user);
		} catch (ResponseEntityException e) {
			response = e.buildResponse();
		} catch (IOException e) {
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (file != null) file.delete();
		}
		return response;		
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
}
