package com.alten.hercules.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class StoreImageTest {
	
	
	private final Path rootLogo = Paths.get("img/logo/");
	
	private final Path rootProj = Paths.get("img/proj/");
	
	public final static String LOGO_FOLDER = "img/logo/";
	public final static String PROJECT_FOLDER = "img/proj/";
	
	/**
	 * Tests of the public methods of the class StoreImage
	 */
	@Test
	void ImageTest() { 
		
		StoreImage storeImage = new StoreImage();
		
		storeImage.init();
		Assert.assertTrue(Files.exists(rootLogo) && Files.exists(rootProj));
		
		MultipartFile file;
		try {
			file = new MockMultipartFile("bluestick.png", "bluestick.png", "image/png", Files.readAllBytes(Paths.get("src/main/resources/bluestick.png")));
			storeImage.save(file, "testImage.png", "logo");
			
			
			Resource resource = storeImage.loadFileAsResource("testImage.png", "logo");
			Assertions.assertTrue(resource.exists() && resource.isFile());
			
			storeImage.delete(rootLogo+"testImage.png");
			Resource resource3 = storeImage.loadFileAsResource("testImage.png", "project");
			Assertions.assertTrue(resource3 == null);
			
			storeImage.save(file, "testImage.png", "logo");
			storeImage.deleteFile("testImage.png", "logo");
			Resource resource4 = storeImage.loadFileAsResource("testImage.png", "project");
			Assertions.assertTrue(resource4 == null);
			
			storeImage.save(file, "testImage.png", "logo");
			storeImage.save(file, "testImage.png", "project");
			Resource resource2 = storeImage.loadFileAsResource("testImage.png", "project");
			storeImage.deleteAll();
			Assertions.assertTrue(!resource2.exists());
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
