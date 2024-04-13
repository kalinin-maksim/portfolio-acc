package edu.kalinin.acc.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import edu.kalinin.acc.configuration.MessageConsumerKafkaConfiguration;
import edu.kalinin.acc.dto.external.input.LoanMessageDTO;
import edu.kalinin.acc.event.MessageEventFabric;
import edu.kalinin.acc.service.MessageProducerService;

import static edu.kalinin.acc.helper.JsonHelper.toJsonSafe;

@Component
@ConditionalOnBean(MessageConsumerKafkaConfiguration.class)
@RequiredArgsConstructor
public class LoanMessageConsumer {

    private final MessageProducerService messageProducerService;
    @Value("${kafka.loan.topic-name}")
    private String topic;

    @KafkaListener(topics = "${kafka.loan.topic-name}", containerFactory = "loanMessageKafkaListenerContainerFactory", groupId = "${kafka.consumers-group}")
    public void receive(LoanMessageDTO message) {
        var accountingId = message.getHeader().getAccountingId();
        MessageEventFabric.observe("LOANS", accountingId, () -> messageProducerService.save(
                topic,
                accountingId,
                toJsonSafe(message)));
    }
}