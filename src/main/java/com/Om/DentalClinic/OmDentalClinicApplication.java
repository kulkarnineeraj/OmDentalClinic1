package com.Om.DentalClinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@EnableJpaRepositories(basePackages = "com.Om.DentalClinic.repository")
@EntityScan(basePackages = "com.Om.DentalClinic.model")
@SpringBootApplication
@ComponentScan(basePackages = {"com.Om.DentalClinic", "com.OmDentalClinic.config"})
public class OmDentalClinicApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(OmDentalClinicApplication.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(OmDentalClinicApplication.class, args);
	}

}
	