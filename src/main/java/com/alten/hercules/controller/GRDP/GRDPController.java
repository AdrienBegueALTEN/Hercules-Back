package com.alten.hercules.controller.GRDP;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.hercules.dal.GRDPDAL;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Class that manages the requests sent to the API for the GRPD.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/hercules/grdp")
public class GRDPController {

	@Autowired
	private GRDPDAL dal;
	
	/**
	 * Function that applies the GRPD to the database, by anonymising the data of the people that left the company for more than 5 years.
	 * @return
	 */
	@ApiOperation(value = "Apply GRDP on all user.", notes = "Set anonymous lastname, firstname and email to the consultants, "
			+ "the manager and recruitment officers that left the compagny for at least 5 years. ")
	@ApiResponses({
		@ApiResponse(code = 200, message="GRPD is applied."),
		@ApiResponse(code = 401, message="Invalid authentificated token or user isn't administrator."),
	})
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping
	public ResponseEntity<?> applyGRDP() {
		dal.applyGRDP();
		return ResponseEntity.ok().build();
	}
}