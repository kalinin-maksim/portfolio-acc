package edu.kalinin.acc.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import edu.kalinin.acc.configuration.MessageConsumerKafkaConfiguration;
import edu.kalinin.acc.dto.Event;
import edu.kalinin.acc.event.MessageEventFabric;
import edu.kalinin.acc.service.MessageProducerService;

import static edu.kalinin.acc.helper.JsonHelper.toJsonSafe;

@Component
@ConditionalOnBean(MessageConsumerKafkaConfiguration.class)
@RequiredArgsConstructor
public class KafKaEventConsumer {

    private final MessageProducerService messageProducerService;
    @Value("${kafka.message.topic-name}")
    private String topic;

    @KafkaListener(topics = "${kafka.message.topic-name}", containerFactory = "messageKafkaListenerContainerFactory", groupId = "${kafka.consumers-group}")
    public void receive(Event event) {
        var accountingId = event.getHeader().getAccountingId();
        MessageEventFabric.observe("FT", accountingId, () -> messageProducerService.save(
                topic,
                accountingId,
                toJsonSafe(event)));
    }
}