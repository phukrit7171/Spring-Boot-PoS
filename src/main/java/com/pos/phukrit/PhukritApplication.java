package com.pos.phukrit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
// Assuming your @Entity classes are in a 'model' or 'entity' sub-package
@EntityScan(basePackages = "com.pos.phukrit.models")
public class PhukritApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhukritApplication.class, args);
    }

}