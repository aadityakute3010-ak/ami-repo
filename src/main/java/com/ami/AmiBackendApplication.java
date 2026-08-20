package com.ami;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AmiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmiBackendApplication.class, args);
	}
}
 