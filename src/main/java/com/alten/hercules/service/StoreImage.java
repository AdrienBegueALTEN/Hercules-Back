package com.alten.hercules.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StoreImage{

	private final Path root = Paths.get("img");

	public void save(MultipartFile file) {
		try {
			Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
		} catch (Exception e) {
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
	
	public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.root.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) 
                return resource;
        } catch (MalformedURLException ex) {
        	System.err.println(ex);
        }
        return null;
    }


}
