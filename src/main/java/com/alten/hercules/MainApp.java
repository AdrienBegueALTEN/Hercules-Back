package com.alten.hercules;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.alten.hercules.controller.user.RecruitementOfficerController;
import com.alten.hercules.dal.ConsultantDAL;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan("com.alten")
public class MainApp {

	public static void main(String[] args) {
		SpringApplication.run(MainApp.class, args);
	}

}
