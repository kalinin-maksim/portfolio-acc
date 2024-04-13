package edu.kalinin.acc.validation.handler.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import edu.kalinin.acc.validation.JsonValidateErrorHandler;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "kafka.message.dead-letter.enabled", havingValue = "false")
public class EventValidateErrorHandler implements JsonValidateErrorHandler {

    private final RestTemplate restTemplate;

    @Value("${kafka.message.dead-letter.url}")
    private String url;

    @Override
    public Void handle(String message, String errorMsg) {
        restTemplate.postForObject(url, message, String.class);
        return null;
    }
}
