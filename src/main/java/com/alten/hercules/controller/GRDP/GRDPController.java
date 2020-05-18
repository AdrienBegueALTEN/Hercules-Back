package com.alten.hercules.controller.GRDP;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.dal.GRDPDAL;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/hercules/grdp")
public class GRDPController {

	@Autowired
	private GRDPDAL dal;
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping
	public ResponseEntity<?> applyGRDP() {
		dal.applyGRDP();
		return ResponseEntity.ok().build();
	}
}