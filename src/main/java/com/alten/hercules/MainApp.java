package com.alten.hercules;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.alten.hercules.controller.UserController;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan("com.company")
@ComponentScan(basePackageClasses = UserController.class)
public class MainApp {

	public static void main(String[] args) {
		SpringApplication.run(MainApp.class, args);
	}

}
