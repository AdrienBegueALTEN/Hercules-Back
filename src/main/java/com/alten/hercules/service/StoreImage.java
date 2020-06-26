package com.alten.hercules.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Class that manages the uploaded picture files.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@Service
public class StoreImage {
	
	/**
	 * Path for the logo folder
	 */
	private final Path rootLogo = Paths.get("img/logo/");
	/**
	 * Path for the proj folder
	 */
	private final Path rootProj = Paths.get("img/proj/");
	
	public final static String LOGO_FOLDER = "img/logo/";
	public final static String PROJECT_FOLDER = "img/proj/";
	
	
	/**
	 * Function that initializes the folders for the pictures.
	 */
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
	
	/**
	 * Function that delete all the pictures in the folders.
	 */
	public void deleteAll() {
	    FileSystemUtils.deleteRecursively(this.rootLogo.toFile());
	    FileSystemUtils.deleteRecursively(this.rootProj.toFile());
	}
	
	/**
	 * Function that saves the file of the given picture file.
	 * @param file file to be saved
	 * @param type String that shows if the file is a picture for a customer or a picture for a project.
	 */
	public void save(MultipartFile file, String name, String type) {
		try {
			Path path = null;
			switch(type) {
			case "logo":
				path = this.rootLogo.resolve(name);
				break;
			case "project":
				path = this.rootProj.resolve(name);
				break;
			}
			if (!Files.exists(path))
				Files.copy(file.getInputStream(), path,
		                StandardCopyOption.REPLACE_EXISTING); 
				//Files.copy(file.getInputStream(), path);
		} catch (Exception e) {
			System.out.println(e.getClass());
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		}
	}
	
	/**
	 * Function that deletes a specific picture file.
	 * @param path path of the file 
	 * @return true if the file was deleted and false if it doesn't exist
	 */
	public boolean delete(String path) {
		try {
			return Files.deleteIfExists(Paths.get(path));
		} catch (Exception e) {
			throw new RuntimeException("Could not delete the file. Error: " + e.getMessage());
		}
	}
	
	/**
	 * Function that gives an URL for a given image.
	 * @param fileName name of the file
	 * @param type String that shows if the file is a picture for a customer or a picture for a project.
	 * @return An URL linked to the given image
	 */
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
	
	/**
	 * Function that deletes a picture file given its name and its type.
	 * @param fileName name of the file
	 * @param type String that shows if the file is a picture for a customer or a picture for a project.
	 */
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
