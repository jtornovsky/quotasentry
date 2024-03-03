package com.api.quotasentry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QuotasentryApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuotasentryApplication.class, args);
	}

}
