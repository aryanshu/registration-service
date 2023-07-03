package com.example.svc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
//@EnableEurekaClient
public class RegistrationApplication {

	private static Logger logger = LoggerFactory.getLogger(RegistrationApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(RegistrationApplication.class, args);
	}

}
