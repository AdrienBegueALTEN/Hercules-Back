package com.alten.hercules.service;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.alten.hercules.model.consultant.Consultant;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.exception.InvalidValueException;
import com.alten.hercules.model.mission.EContractType;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.mission.MissionSheet;
import com.alten.hercules.model.project.Project;
import com.alten.hercules.model.user.Manager;

class PDFGeneratorTest {
	
	
	static Mission missionTest;
	static PDDocument document;
	static Set<Project> projects;
	
	/**
	 * Initialization for the tests
	 */
	@BeforeAll
	static void initializationTest() {
		document = new PDDocument();
		
		try {
			missionTest = new Mission(new Consultant("consultant.consultant@alten.com","consultant","consultant",new Manager("mana.mana@alten.com","mana","mana",true)), 
									  new Customer("Customer","Activity","Description"));
			missionTest.setId((long) 12);
			
			MissionSheet sheet = new MissionSheet(missionTest);
			sheet.setCity("Kyoto");
			sheet.setComment("Comment");
			sheet.setConsultantRole("Rôle");
			sheet.setConsultantStartXp(1);
			sheet.setContractType(EContractType.services_center);
			sheet.setCountry("Japan");
			sheet.setDescription("description");
			sheet.setTeamSize(6);
			sheet.setTitle("Title");
			sheet.setVersionDate(LocalDate.of(2020, 9, 23));
			sheet.setId((long) 12);
			
			Project projectTest = new Project(sheet);
			projectTest.setTitle("Title");
			projectTest.setDescription("Description");
			projectTest.setBeginDate(LocalDate.of(2020, 3, 3));
			projectTest.setEndDate(LocalDate.of(2020, 4, 1));
			
			
			projects = new HashSet<Project>(Arrays.asList(projectTest));
			
			sheet.setProjects(projects);
			
			Set<MissionSheet> sheets = new HashSet<MissionSheet>();
			sheets.add(sheet);
			
			missionTest.setVersions(sheets);
			
		} catch (InvalidValueException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Tests of the 5 public methods from the class PDFGenerator
	 */
	@Test
	void PDFTest() { 
		
		
		try {
			
			PDFGenerator.init();
			
			PDFGenerator pdfgen;
			pdfgen = new PDFGenerator(document);
			Assertions.assertTrue(pdfgen!=null);
	
			
			pdfgen.makePDFPage(missionTest, missionTest.getLastVersion().getProjects(), document, true);
			pdfgen.makePDFPage(projects.iterator().next().getMissionSheet().getMission(),
					           projects,document, false);
			pdfgen.saveFinalPDF(document);
			
			File f = new File("pdf\\fichesMissionsEtProjets.pdf");
			Assertions.assertTrue(f.exists() && !f.isDirectory());
			
			PDFGenerator.deleteAll();
			
			Assertions.assertTrue(!f.exists());
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
