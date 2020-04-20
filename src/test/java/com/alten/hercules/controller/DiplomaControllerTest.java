package com.alten.hercules.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alten.hercules.controller.consultant.ConsultantController;
import com.alten.hercules.controller.diploma.DiplomaController;
import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.dao.diploma.DiplomaDAO;
import com.alten.hercules.dao.diploma.DiplomaLocationDAO;
import com.alten.hercules.dao.diploma.DiplomaNameDAO;
import com.alten.hercules.dao.diploma.LevelDAO;
import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.diploma.*;
import com.alten.hercules.model.request.DiplomaRequest;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DiplomaControllerTest {

	@InjectMocks
	DiplomaController controller;
	
	@Mock
	DiplomaDAO diplDAO;
	
	@Mock
	DiplomaLocationDAO locDAO;
	
	@Mock
	DiplomaNameDAO nameDAO;
	
	@Mock
	LevelDAO levDAO;
	
	@Test
	void testAddDiplomaOk() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		Level level = new Level("ingé 3");
		DiplomaName dn = new DiplomaName("ingé info", level);
		DiplomaLocation loc = new DiplomaLocation("stq");
		
		
		DiplomaRequest req = new DiplomaRequest(2020,"stq","ingé info","ingé 3");
		
		when(levDAO.findByName("")).thenReturn(level);
		when(nameDAO.findByName("")).thenReturn(dn);
		when(locDAO.findByCity("")).thenReturn(loc);
		when(diplDAO.findDiplome(2020, "stq", "ingé info", "ingé3")).thenReturn(null);
		
		when(diplDAO.save(new Diploma(2020,loc,dn))).thenReturn(null);
		
		ResponseEntity<?> responseEntity =  controller.addDiploma(req);
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
	}
	
	@Test
	void testAddDiplomaConflict() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		Level level = new Level("ingé 3");
		DiplomaName dn = new DiplomaName("ingé info", level);
		DiplomaLocation loc = new DiplomaLocation("stq");
		
		
		DiplomaRequest req = new DiplomaRequest(2020,"stq","ingé info","ingé 3");
		
		when(levDAO.findByName("")).thenReturn(level);
		when(nameDAO.findByName("")).thenReturn(dn);
		when(locDAO.findByCity("")).thenReturn(loc);
		when(diplDAO.findDiplome(2020, "stq", "ingé info", "ingé3")).thenReturn(new Diploma());
		
		when(diplDAO.save(new Diploma(2020,loc,dn))).thenReturn(null);
		
		ResponseEntity<?> responseEntity =  controller.addDiploma(req);
		assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
	}

}
