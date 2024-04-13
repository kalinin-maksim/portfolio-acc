package edu.kalinin.acc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApplicationOutMessageProducer {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationOutMessageProducer.class, args);
    }
}