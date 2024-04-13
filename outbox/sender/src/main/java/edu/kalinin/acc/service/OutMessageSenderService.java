package edu.kalinin.acc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import edu.kalinin.acc.entity.OutMessage;
import edu.kalinin.acc.repository.OutMessageRepository;
import edu.kalinin.acc.selector.Channel;

import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutMessageSenderService {

    public static final Pattern MSG_RECEIVED_PATTERN = Pattern.compile("Message.*received");

    @Value("${kafka.producer-group}")
    private String producersGroup;

    private final OutMessageRepository outMessageRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send() {
        var messagesByProcessId = outMessageRepository.findByDeletedFalseAndChannel(Channel.KAFKA)
                .stream()
                .collect(Collectors.groupingBy(OutMessage::getProcessId));
        for (var entry : messagesByProcessId.entrySet()) {
            var processId = entry.getKey();
            var messages = entry.getValue();
            for (var message : messages) {
                if (message.getMessage().contains("double")) {
                    outMessageRepository.findAllByProcessIdAndDeletedTrue(processId).stream()
                            .filter(outMessage -> MSG_RECEIVED_PATTERN.matcher(outMessage.getMessage()).find())
                            .max(Comparator.comparing(OutMessage::getCreatedMoment))
                            .ifPresent(lastMessage -> {
                                repeat(lastMessage);
                                delete(message);
                            });
                } else {
                    send(message);
                }
            }
        }
    }

    private void send(OutMessage outMessage) {
        var message = MessageBuilder
                .withPayload(outMessage.getResponse())
                .setHeader(KafkaHeaders.TOPIC, outMessage.getAddress())
                .setHeader("producers-group", producersGroup)
                .build();
        kafkaTemplate.send(message);
        delete(outMessage);
    }

    private void delete(OutMessage outMessage) {
        outMessage.setDeleted(true);
        outMessageRepository.save(outMessage);
    }

    private void repeat(OutMessage outMessage) {
        var message = MessageBuilder
                .withPayload(outMessage.getResponse())
                .setHeader(KafkaHeaders.TOPIC, outMessage.getAddress())
                .setHeader("producers-group", producersGroup)
                .build();
        kafkaTemplate.send(message);
    }
}
