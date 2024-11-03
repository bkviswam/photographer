package com.algo.dev.photographer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication(scanBasePackages = {"com.algo.dev.photographer"})
@EnableCaching
@Validated
public class PhotographerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotographerServiceApplication.class, args);
	}

}
