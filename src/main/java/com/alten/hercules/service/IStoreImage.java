package com.alten.hercules.service;

import org.springframework.web.multipart.MultipartFile;

public interface IStoreImage {
	public void save(MultipartFile file);
	boolean delete(String path);

}
