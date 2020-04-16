package com.alten.hercules.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.alten.hercules.dao.consultant.ConsultantDAO;
import com.alten.hercules.model.consultant.Consultant;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConsultantControllerTest {

	@InjectMocks
	ConsultantController controller;
	
	@Mock
	ConsultantDAO dao;
	
	/* ADD */
	
	@Test
	void testAddConsultantNoEmail() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		Consultant c1 = new Consultant();
		when(dao.save(c1)).thenReturn(c1);
		ResponseEntity<?> responseEntity =  controller.addConsultant(c1);
		assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
	}
	
	
	void testAddConsultantEmailExists() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		Consultant c1 = new Consultant(2,"a@a.fr","rob","jes","form","utbm","JD",true,1,null);
		
		//findByEmail found something already existing
		when(dao.findByEmail("a@a.fr")).thenReturn(new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,1,null));
		when(dao.save(c1)).thenReturn(c1);
		
		ResponseEntity<?> responseEntity =  controller.addConsultant(c1);
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
	}
	
	@Test
	void testAddConsultantOk() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		Consultant c1 = new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,1,null);
		//findByEmail found nothing so the consultant can be created
		when(dao.findByEmail("a@a.fr")).thenReturn(null);
		when(dao.save(c1)).thenReturn(c1);
		ResponseEntity<?> responseEntity =  controller.addConsultant(c1);
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
	}
	
	/* UPDATE */
	
	@Test
	void testUpdateConsultantNotFound() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		Consultant c1 = new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,1,null);
		when(dao.findById(1)).thenReturn(null);
		ResponseEntity<?> responseEntity =  controller.updateConsultant(c1);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}
	
	@Test
	void testUpdateConsultantOk() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		Consultant c1 = new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,1,null);
		when(dao.findById(1)).thenReturn(c1);
		when(dao.save(c1)).thenReturn(c1);
		ResponseEntity<?> responseEntity =  controller.updateConsultant(c1);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}
	
	/* DEACTIVATE */
	
	@Test
	void testDeactivateBadRequest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		Consultant c1 = new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,1,null);
		Date date=null;
		
		when(dao.findById(1)).thenReturn(c1);
		when(dao.save(c1)).thenReturn(c1);
		
		ResponseEntity<?> responseEntity =  controller.deactivate(date, c1);
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}
	
	@Test
	void testDeactivateNotFound() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		Consultant c1 = new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,1,null);
		Date date=new Date(2020,4,16);
		
		when(dao.findById(1)).thenReturn(null);
		when(dao.save(c1)).thenReturn(c1);
		
		ResponseEntity<?> responseEntity =  controller.deactivate(date, c1);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}
	
	@Test
	void testDeactivateOk() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		Consultant c1 = new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,1,null);
		Date date=new Date(2020,4,16);
		
		when(dao.findById(1)).thenReturn(c1);
		when(dao.save(c1)).thenReturn(c1);
		
		ResponseEntity<?> responseEntity =  controller.deactivate(date, c1);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertFalse(c1.isEnabled());
	}
	
	/* DELETE */
	
	@Test
	void testDeleteConsultantNotFound() {
		Consultant c1 = new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,1,null);
		
		when(dao.findById(1)).thenReturn(null);
		ResponseEntity<?> responseEntity =  controller.deleteConsultant( c1);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
		
	}
	
	@Test
	void testDeleteConsultantWithMission() {
		fail(); //TODO
	}
	
	@Test
	void testDeleteConsultantOk() {
Consultant c1 = new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,1,null);
		
		when(dao.findById(1)).thenReturn(c1);
		ResponseEntity<?> responseEntity =  controller.deleteConsultant( c1);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	/* GET ALL */
	
	@Test
	void testGetAllConsultants() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		List<Consultant> cons = new ArrayList<>();
		cons.add(new Consultant());
		cons.add(new Consultant());
		cons.add(new Consultant());
		
		when(dao.findAll()).thenReturn(cons);
		
		List<Consultant> res = controller.getAllConsultants();
		assertEquals(3, res.size());
	}
	
	@Test
	void testGetAllConsultantsEmpty() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		List<Consultant> cons = new ArrayList<>();
		
		when(dao.findAll()).thenReturn(cons);
		
		List<Consultant> res = controller.getAllConsultants();
		assertEquals(0, res.size());
	}
	
	/* GET BY ID */
	
	@Test
	void testGetByIdNotFound() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		when(dao.findById(1)).thenReturn(null);
		
		ResponseEntity<?> responseEntity =  controller.getById(1);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}
	
	@Test
	void testGetByIdOk() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		Consultant c1 = new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,1,null);
		when(dao.findById(1)).thenReturn(c1);
		
		ResponseEntity<?> responseEntity =  controller.getById(1);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}
	
	/* GET MLANAGER OF CONSULTANT */
	
	@Test
	void testGetManagerOfConsultantNotFound() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		Consultant c1 = new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,10,null);
		when(dao.findById(1)).thenReturn(null);
		
		ResponseEntity<Integer> responseEntity =  controller.getManagerOfConsultant(1);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}
	
	@Test
	void testGetManagerOfConsultantOk() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		Consultant c1 = new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,10,null);
		when(dao.findById(1)).thenReturn(c1);
		
		ResponseEntity<Integer> responseEntity =  controller.getManagerOfConsultant(1);
		System.out.println(responseEntity);
		//assertEquals(c1.getIdManager(), responseEntity.getBody()); //TODO
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}
	
	/* SEARCH */
	
	@Test
	void testSearch() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		List<Consultant> cons = new ArrayList<>();
		cons.add(new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,10,null));
		cons.add(new Consultant(1,"a@a.fr","adr","beg","form","utbm","JD",true,10,null));
		cons.add(new Consultant(1,"a@a.fr","jul","bau","form","utbm","JD",true,10,null));
		
		List<Consultant> rList = new ArrayList<>() ;
		rList.add(cons.get(0));
		rList.add(cons.get(1));
		List<Consultant> jesList = new ArrayList<>() ;
		jesList.add(cons.get(0));
		
		when(dao.findByLastnameOrFirstname("r")).thenReturn(rList);
		when(dao.findByLastnameOrFirstname("jes")).thenReturn(jesList);
		
		List<String> keys = new ArrayList<>() ;
		keys.add("r");
		keys.add("jes");
		List<Consultant> res = controller.searchConsultant(keys);
		assertEquals(2, res.size());
	}
	
	@Test
	void testSearchEmpty() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		List<Consultant> cons = new ArrayList<>();
		cons.add(new Consultant(1,"a@a.fr","rob","jes","form","utbm","JD",true,10,null));
		cons.add(new Consultant(1,"a@a.fr","adr","beg","form","utbm","JD",true,10,null));
		cons.add(new Consultant(1,"a@a.fr","jul","bau","form","utbm","JD",true,10,null));
		
		List<Consultant> emptyList = new ArrayList<>() ;
		
		when(dao.findByLastnameOrFirstname("robin")).thenReturn(emptyList);
		
		List<String> keys = new ArrayList<>() ;
		keys.add("robin");
		List<Consultant> res = controller.searchConsultant(keys);
		assertEquals(0, res.size());
	}
	
	
}