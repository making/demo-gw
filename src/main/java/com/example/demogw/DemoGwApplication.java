package com.example.demogw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoGwApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoGwApplication.class, args);
    }

}
