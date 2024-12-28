// usage-management-service/src/main/java/com/ktds/krater/management/UsageManagementApplication.java
package com.ktds.krater.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class UsageManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsageManagementApplication.class, args);
    }
}