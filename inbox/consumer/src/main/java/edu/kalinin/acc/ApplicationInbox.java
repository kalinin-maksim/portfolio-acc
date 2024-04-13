package edu.kalinin.acc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "edu.kalinin.acc",
        "edu.kalinin.acc.crypto.lib.api"})
public class ApplicationInbox {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationInbox.class, args);
    }
}