package com.ant.juchumjuchum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JuchumjuchumApplication {

    public static void main(String[] args) {
        SpringApplication.run(JuchumjuchumApplication.class, args);
    }

}
