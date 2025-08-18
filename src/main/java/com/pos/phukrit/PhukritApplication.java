package com.pos.phukrit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.pos.phukrit")
public class PhukritApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhukritApplication.class, args);
	}

}
