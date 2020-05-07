package com.alten.hercules.controller.auth;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.controller.auth.http.request.LoginRequest;
import com.alten.hercules.dal.AuthenticationDAL;
import com.alten.hercules.model.exception.RessourceNotFoundException;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.response.JWTResponse;
import com.alten.hercules.model.user.AppUser;
import com.alten.hercules.security.jwt.JwtUtils;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/hercules/auth")
public class AuthController {
	
	@Autowired AuthenticationManager authManager;
	@Autowired AuthenticationDAL dal;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {
		Authentication authentication = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return ResponseEntity.ok(new JWTResponse((AppUser)authentication.getPrincipal()));
	}
	
	@PreAuthorize("hasAuthority('MANAGER')")
	@GetMapping("generate-access-token/{missionId}")
	public ResponseEntity<?> getAnonymousTokenForMission(@PathVariable Long missionId) {
		try {
			Mission mission = dal.findMissionById(missionId)
					.orElseThrow(() -> new RessourceNotFoundException("mission"));
			dal.changeMissionSecret(mission);
			return ResponseEntity.ok(JwtUtils.generateJwt(mission));
		} catch (RessourceNotFoundException e) {
			return e.buildResponse();
		}		
	}
}
