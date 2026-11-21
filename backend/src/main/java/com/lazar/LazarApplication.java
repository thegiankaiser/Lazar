package com.lazar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LazarApplication {
    public static void main(String[] args) {
        SpringApplication.run(LazarApplication.class, args);
    }
}
