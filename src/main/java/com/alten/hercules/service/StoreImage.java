package com.alten.hercules.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StoreImage {

	private final Path rootLogo = Paths.get("img/logo/");
	private final Path rootProj = Paths.get("img/proj/");
	
	public final static String LOGO_FOLDER = "img/logo/";
	public final static String PROJECT_FOLDER = "img/proj/";

	public void init() {
		try {
			if(!Files.exists(this.rootLogo))
				Files.createDirectories(rootLogo);
			if(!Files.exists(this.rootProj))
				Files.createDirectories(rootProj);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload!");
		}
	}
	
	public void deleteAll() {
	    FileSystemUtils.deleteRecursively(this.rootLogo.toFile());
	    FileSystemUtils.deleteRecursively(this.rootProj.toFile());
	}

	public void save(MultipartFile file, String type) {
		try {
			Path path = null;
			switch(type) {
			case "logo":
				path = this.rootLogo.resolve(file.getOriginalFilename());
				break;
			case "project":
				path = this.rootProj.resolve(file.getOriginalFilename());
				break;
			}
			if (!Files.exists(path))
				Files.copy(file.getInputStream(), path);
		} catch (Exception e) {
			System.out.println(e.getClass());
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		}
	}

	public boolean delete(String path) {
		try {
			return Files.deleteIfExists(Paths.get(path));
		} catch (Exception e) {
			throw new RuntimeException("Could not delete the file. Error: " + e.getMessage());
		}
	}

	public Resource loadFileAsResource(String fileName, String type) {
		try {
			Path filePath = null;
			switch(type) {
			case "logo":
				filePath = this.rootLogo.resolve(fileName).normalize();
				break;
			case "project":
				filePath = this.rootProj.resolve(fileName).normalize();
				break;
			}
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists())
				return resource;
		} catch (MalformedURLException ex) {
			System.err.println(ex);
		}
		return null;
	}
	
	public void deleteFile(String fileName, String type) {
		Path filePath = null;
		switch(type) {
		case "logo":
			filePath = this.rootLogo.resolve(fileName).normalize();
			break;
		case "project":
			filePath = this.rootProj.resolve(fileName).normalize();
			break;
		}
		try {
			Files.delete(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
