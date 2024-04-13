package edu.kalinin.acc.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "integration", ignoreUnknownFields = false)
@Getter
@Setter
public class IntegrationConfiguration {

    private Map<String, String> topicsIn;
    private Map<String, String> topicsOut;


    public String getTopicIn(String sender) {
        return topicsIn.get(sender);
    }

    public String getTopicOut(String receiver) {
        return topicsOut.get(receiver);
    }
}