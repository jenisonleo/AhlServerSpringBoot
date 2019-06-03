package com.comcom.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@SpringBootApplication
public class AhlServerSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(AhlServerSpringApplication.class, args);
	}

}
