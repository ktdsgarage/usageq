package com.ktds.krater.update;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.ktds.krater.common.entity")
@EnableJpaRepositories
public class UsageUpdateApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsageUpdateApplication.class, args);
    }
}
