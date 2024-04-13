package edu.kalinin.acc.consumer;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import edu.kalinin.acc.ApplicationInbox;
import edu.kalinin.acc.entity.Message;
import edu.kalinin.acc.repository.MessageRepository;
import edu.kalinin.acc.validation.handler.kafka.EventValidateErrorHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static edu.kalinin.acc.helper.JsonHelper.toJson;
import static edu.kalinin.acc.helper.TestDataFactory.EventFactory.ACCOUNTING_ID;
import static edu.kalinin.acc.helper.TestDataFactory.EventFactory.getEvent;

@ContextConfiguration(classes = {ApplicationInbox.class})
@SpringBootTest
@ActiveProfiles({
        "kafka-local",
        "kafka-consumer-test"
})
@DirtiesContext
@EmbeddedKafka(topics = "${kafka.message.topic-name}", partitions = 1)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.HSQLDB)
class ValidMessageTest {
    @Value("${kafka.message.topic-name}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    private EventValidateErrorHandler eventValidateErrorHandler;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    @SneakyThrows
    void listenerTest() {

        var messageLatch = new CountDownLatch(1);

        when(messageRepository.save(any())).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            synchronized (messageLatch) {
                messageLatch.countDown();
                return message;
            }
        });

        var message = MessageBuilder
                .withPayload(toJson(getEvent()))
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
        kafkaTemplate.send(message);
        kafkaTemplate.flush();

        then(messageLatch.await(60, TimeUnit.SECONDS)).isTrue();

        synchronized (messageLatch) {
            var messageCaptor = ArgumentCaptor.forClass(Message.class);
            verify(messageRepository).save(messageCaptor.capture());
            then(messageCaptor.getValue().getMsgId()).isEqualTo(ACCOUNTING_ID);
            verify(eventValidateErrorHandler, times(0)).handle(any(), any());
        }
    }

}
