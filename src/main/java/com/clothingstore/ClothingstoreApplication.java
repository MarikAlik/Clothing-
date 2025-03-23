package com.clothingstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.clothingstore.repository")
public class ClothingstoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClothingstoreApplication.class, args);
    }
}



