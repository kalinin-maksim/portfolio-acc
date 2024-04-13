package edu.kalinin.acc.validation.handler.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import edu.kalinin.acc.event.MessageEventFabric;
import edu.kalinin.acc.validation.JsonValidateErrorHandler;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "kafka.message.dead-letter.enabled", havingValue = "true")
public class EventValidateErrorHandler implements JsonValidateErrorHandler {

    @Override
    public Void handle(String message, String errorMsg) {
        MessageEventFabric.createInvalidatedMessageEvent(message, errorMsg);
        return null;
    }
}
