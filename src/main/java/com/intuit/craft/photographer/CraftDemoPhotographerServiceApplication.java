package com.intuit.craft.photographer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication(scanBasePackages = {"com.intuit.craft.photographer"})
@EnableCaching
@Validated
public class CraftDemoPhotographerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CraftDemoPhotographerServiceApplication.class, args);
	}

}
