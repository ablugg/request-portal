package com.github.ablugg.requestportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point. @SpringBootApplication tells Spring to scan this package and everything
 * under it for controllers, services and repositories, and to auto-configure the web
 * server and database connection from what is on the classpath.
 */
@SpringBootApplication
public class RequestPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequestPortalApplication.class, args);
    }
}
