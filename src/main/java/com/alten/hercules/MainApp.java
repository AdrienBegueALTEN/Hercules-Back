package com.alten.hercules;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.alten.hercules.controller.user.RecruitementOfficerController;
import com.alten.hercules.dal.ConsultantDAL;
import com.alten.hercules.service.StoreImage;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan("com.alten")
public class MainApp implements CommandLineRunner  {

	@Resource
	private StoreImage storeImage;
	public static void main(String[] args) {
		SpringApplication.run(MainApp.class, args);
	}

	@Override
	public void run(String... arg) throws Exception {
		this.storeImage.deleteAll();
		this.storeImage.init();
	}

}
