package edu.kalinin.acc.controler.dumb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/kafka")
public class ProducerController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping(path = "/message", consumes = "application/json")
    public void post(@RequestParam String topicName, @RequestBody String[] messages) {
        for (var message : messages) {
            kafkaTemplate.send(getStringMessage(message, topicName));
            kafkaTemplate.flush();
        }
    }

    private Message<String> getStringMessage(String message, String topicName) {
        var msg = MessageBuilder
                .withPayload(message)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
        return msg;
    }
}