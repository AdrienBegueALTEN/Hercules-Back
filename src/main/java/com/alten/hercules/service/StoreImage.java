package com.alten.hercules.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StoreImage implements IStoreImage {

	private final Path root = Paths.get("img");

	@Override
	public void save(MultipartFile file) {
		try {
			System.out.println(file.getOriginalFilename());
			Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
		} catch (Exception e) {
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		}
	}
	
	@Override
	public boolean delete(String path) {
		try {
			return Files.deleteIfExists(Paths.get(path));
		} catch (Exception e) {
			throw new RuntimeException("Could not delete the file. Error: " + e.getMessage());
		}
	}


}
