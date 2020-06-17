package com.alten.hercules.controller.authentification;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

import com.alten.hercules.controller.authentification.http.request.ChangePasswordRequest;
import com.alten.hercules.controller.authentification.http.request.LoginRequest;
import com.alten.hercules.dal.AuthenticationDAL;
import com.alten.hercules.model.exception.InvalidSheetStatusException;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.exception.ResourceNotFoundException;
import com.alten.hercules.model.exception.ResponseEntityException;
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.response.JWTResponse;
import com.alten.hercules.model.user.AppUser;
import com.alten.hercules.utils.EmlFileUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/auth")
public class AuthentificationController {
	
	@Autowired private AuthenticationManager authManager;
	@Autowired private AuthenticationDAL  dal;
	
	@ApiOperation(
			value = "User authentification with login/password.",
			notes = "Return an authentification token which contains the identifier, the firstname, the lastname and the roles of the authentificated user."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message="Authentification succeeded."),
		@ApiResponse(code = 401, message="Authentificated failed.")
	})
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(
			@ApiParam(
					"email : user's email (login);\n"
					+ "password : user's password."
			)
			@Valid @RequestBody LoginRequest request) {
		Authentication authentication = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return ResponseEntity.ok(new JWTResponse((AppUser)authentication.getPrincipal()));
	}
	
	@ApiOperation(
			value = "Check password modification token validity (anonymous user).",
			notes = "Check the validity of a password modification token.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Valid token."),
		@ApiResponse(code = 401, message="Invalid token.")
	})
	@PreAuthorize("hasAuthority('CHANGE_PASSWORD')")
	@GetMapping("/change-password-anonymous")
	public ResponseEntity<?> checkTokenValidity() {
		return ResponseEntity.ok(null);
	}
	
	@ApiOperation(
			value = "Update user password (anonymous user).",
			notes = "Update the password of an user without needs of an login/password authentification.\n"
			+ "Return an authentification token which contains the identifier, the firstname, the lastname and the roles of the user.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Password updated."),
		@ApiResponse(code = 400, message="New password is empty.")
	})
	@PreAuthorize("hasAuthority('CHANGE_PASSWORD')")
	@PutMapping("/change-password-anonymous")
	public ResponseEntity<?> changePasswordAnonymous(
			@ApiParam("user's new password.")
			@RequestBody String newPassword) {
		try {
			AppUser user = (AppUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
			user = dal.findUserById(user.getId()).get();
			user.setPassword(newPassword);
			user = dal.saveUser(user);
			return ResponseEntity.ok(new JWTResponse(user));
		} catch (InvalidValueException e) { return e.buildResponse(); }
	}
	
	@ApiOperation(
			value = "Update user password.",
			notes = "Update the password of an authentificated user.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Password updated."),
		@ApiResponse(code = 400, message="New password is empty."),
		@ApiResponse(code = 401, message="Invalid authentificated token."),
		@ApiResponse(code = 403, message="Real current password does not match with the given one.")
	})
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECRUITMENT_OFFICER')")
	@PutMapping("/change-password")
	public ResponseEntity<?> changePassword(
			@ApiParam(
					"currentPassword : user's current password;\n"
					+ "newPassword : user's new password."
			)
			@Valid @RequestBody ChangePasswordRequest request) {
		try {
			AppUser user = (AppUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
			String providedCurrentPassword = request.getCurrentPassword();
			if (!new BCryptPasswordEncoder().matches(providedCurrentPassword, user.getPassword()))
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The provided password doesn't match with the current password.");
			user.setPassword(request.getNewPassword());
			user = dal.saveUser(user);
			return ResponseEntity.ok(null);
		} catch (InvalidValueException e) { return e.buildResponse(); }
	}
	
	@ApiOperation(
			value = "Generate mission sheet external access.",
			notes = "Return a ready-to-send email which contains an access link to the last sheet of a mission.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Email created."),
		@ApiResponse(code = 401, message="Invalid authentificated token or user isn't manager."),
		@ApiResponse(code = 403, message="User isn't the manager of the consultant linked to the mission or this one is validated."),
		@ApiResponse(code = 404, message="Mission not found."),
		@ApiResponse(code = 500, message="Error occured during the email creation.")
	})
	@PreAuthorize("hasAuthority('MANAGER')")
	@GetMapping("mission-sheet-access/{missionId}")
	public ResponseEntity<?> missionSheetExternalAccess(
			@ApiParam("Identifier of the mission whose access need to be granted.")
			@PathVariable Long missionId) {
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
					.body("Only the manager of the consultant linked to this mission can do this action.");
		} catch (ResponseEntityException e) {
			response = e.buildResponse();
		} catch (IOException e) {
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (file != null) file.delete();
		}
		return response;		
	}
	
	@ApiOperation(
			value = "Generate mission sheet external access.",
			notes = "Return a ready-to-send email which contains an access link to update the password of an user without needs of an login/password authentification.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Email created."),
		@ApiResponse(code = 401, message="Invalid authentificated token or user isn't administrator."),
		@ApiResponse(code = 404, message="User not found."),
		@ApiResponse(code = 500, message="Error occured during the email creation.")
	})
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("password-creation-access/{userId}")
	public ResponseEntity<?> getAnonymousTokenForMission(
			@ApiParam("Identifier of the user whose password is to be changed.")
			@PathVariable Long userId) {
		File file = null;
		ResponseEntity<?> response = null;
		try {
			AppUser user = dal.findUserById(userId)
					.orElseThrow(() -> new ResourceNotFoundException("User"));
			file = EmlFileUtils.genereateEmlFileWithPasswordCreationLink(user).orElseThrow();
			response = buildEmlFileResponse(file);
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
