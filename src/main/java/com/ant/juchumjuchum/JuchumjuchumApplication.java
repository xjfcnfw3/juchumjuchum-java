package com.ant.juchumjuchum;

import com.ant.juchumjuchum.config.StockAccountProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties(StockAccountProperties.class)
@SpringBootApplication
public class JuchumjuchumApplication {

    public static void main(String[] args) {
        SpringApplication.run(JuchumjuchumApplication.class, args);
    }

}
