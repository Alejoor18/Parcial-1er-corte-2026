package com.parcial.backend;

import infrastructure.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {
        "com.parcial.backend",
        "application",
        "domain",
        "infrastructure"
})
@Import(SecurityConfig.class)
public class ParcialApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParcialApplication.class, args);
    }
}