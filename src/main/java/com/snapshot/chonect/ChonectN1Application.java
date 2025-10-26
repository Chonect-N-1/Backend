package com.snapshot.chonect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChonectN1Application {

	private static final Logger logger = LoggerFactory.getLogger(ChonectN1Application.class);

	public static void main(String[] args) {
		SpringApplication.run(ChonectN1Application.class, args);
		logger.info("Application started...");
	}

}
