package com.ktds.krater.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.ktds.krater.common.entity")
@EnableJpaRepositories
@EnableCaching
public class UsageQueryApplication {
   public static void main(String[] args) {
       SpringApplication.run(UsageQueryApplication.class, args);
   }
}
