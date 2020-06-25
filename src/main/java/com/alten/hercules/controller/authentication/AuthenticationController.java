package com.alten.hercules.controller.authentication;

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

import com.alten.hercules.controller.authentication.http.request.ChangePasswordRequest;
import com.alten.hercules.controller.authentication.http.request.LoginRequest;
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

/**
 * Class that manages the requests sent to the API for the authentication.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/auth")
public class AuthenticationController {
	
	/**
	 * Object that manages the authentication operations
	 */
	@Autowired private AuthenticationManager authManager;
	
	/**
	 * DAL for the authentication
	 */
	@Autowired private AuthenticationDAL  dal;
	
	/**
	 * Function that manages the request of authentication from a user.
	 * @param request Request that contains the information of the user for the login
	 * @return 200 The authentication is a success<br> 401 The Authentication failed.
	 */
	@ApiOperation(
			value = "User authentication with login/password.",
			notes = "Return an authentication token which contains the identifier, the firstname, the lastname and the roles of the authenticated user."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message="Authentication succeeded."),
		@ApiResponse(code = 401, message="Authentication failed.")
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
	
	/**
	 * Function that checks from a request the token for modifying a password.
	 * @return 200 The token is accepted<br> 401 The token is rejected.
	 */
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
	
	/**
	 * Function that receives a new password from a request and tries to modify it for an unauthenticated user.
	 * @param newPassword String that contains the new password
	 * @return 200 The password has been modified<br>400 The new password is not accepted.
	 */
	@ApiOperation(
			value = "Update user password (anonymous user).",
			notes = "Update the password of an user without needs of an login/password authentication.\n"
			+ "Return an authentication token which contains the identifier, the firstname, the lastname and the roles of the user.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Password updated."),
		@ApiResponse(code = 400, message="New password is empty or not acceptable.")
	})
	@PreAuthorize("hasAuthority('CHANGE_PASSWORD')")
	@PutMapping("/change-password-anonymous")
	public ResponseEntity<?> changePasswordAnonymous(
			@ApiParam("User's new password.")
			@RequestBody String newPassword) {
		try {
			AppUser user = (AppUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
			user = dal.findUserById(user.getId()).get();
			user.setPassword(newPassword);
			user = dal.saveUser(user);
			return ResponseEntity.ok(new JWTResponse(user));
		} catch (InvalidValueException e) { return e.buildResponse(); }
	}
	
	/**
	 * Function that receives a new password from a request and tries to modify it for an authenticated user verifying that the current password is known.
	 * @param request Request that contains the old and new password
	 * @return 200 The password is updated<br>400 The new password is not accepted<br>401 The authentication is not good<br>403 The given current password is false.
	 */
	@ApiOperation(
			value = "Update user password.",
			notes = "Update the password of an authenticated user.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Password updated."),
		@ApiResponse(code = 400, message="New password is empty."),
		@ApiResponse(code = 401, message="Invalid authenticated token."),
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
	
	/**
	 * Function that creates an email containing a link for accessing a specific mission sheet and modify it.
	 * @param missionId ID of the mission.
	 * @return 200 The email with the link is created<br>401 Invalid rights or token<br>403 The user is not the manager linked to the mission or the mission is already validated<br>404 The mission is not found<br>500 Error in the email's creation.
	 */
	@ApiOperation(
			value = "Generate mission sheet external access.",
			notes = "Return a ready-to-send email which contains an access link to the last sheet of a mission.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Email created."),
		@ApiResponse(code = 401, message="Invalid authenticated token or user isn't manager."),
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
					.orElseThrow(() -> new ResourceNotFoundException(Mission.class));
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
	
	/**
	 * Function that creates an email containing a link for modifying the password of a user.
	 * @param userId ID of the user that needs to change his password
	 * @return 200 The email is created with the link<br>401 Invalid rights or token<br>404 The user is not found<br>500 Error in the email's creation.
	 */
	@ApiOperation(
			value = "Generate an email with a link to update the password.",
			notes = "Return a ready-to-send email which contains an access link to update the password of an user without needs of an login/password authentication.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Email created."),
		@ApiResponse(code = 401, message="Invalid authenticated token or user isn't administrator."),
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
					.orElseThrow(() -> new ResourceNotFoundException(AppUser.class));
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
	
	/**
	 * Function that gets the eml file for an email and sends it back in the response to the request.
	 * @param file eml file
	 * @return 200 The eml file is sent in the response
	 * @throws IOException Exception for an error of handling with the file.
	 */
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
