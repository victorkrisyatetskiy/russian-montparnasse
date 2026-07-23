package com.russianmontparnasse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RussianMontparnasseApplication {

	public static void main(String[] args) {
		SpringApplication.run(RussianMontparnasseApplication.class, args);
	}
}