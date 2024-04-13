package edu.kalinin.acc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApplicationGeneralLedger {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationGeneralLedger.class, args);
    }
}