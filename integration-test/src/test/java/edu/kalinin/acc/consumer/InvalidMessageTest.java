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
import edu.kalinin.acc.entity.ResponseCode;
import edu.kalinin.acc.repository.MessageRepository;
import edu.kalinin.acc.repository.OutMessageRepository;
import edu.kalinin.acc.repository.ResponseCodeRepository;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static edu.kalinin.acc.helper.JsonHelper.toJson;
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
class InvalidMessageTest {
    @Value("${kafka.message.topic-name}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ResponseCodeRepository responseCodeRepository;

    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    private OutMessageRepository outMessageRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    @SneakyThrows
    void listenerWrongEventTest() {

        var responseCode = new ResponseCode();
        responseCode.setCode(BigDecimal.TEN);
        responseCode.setRegExp(".*does not match the regex pattern.*");
        responseCodeRepository.save(responseCode);

        var messageLatch = new CountDownLatch(1);

        when(outMessageRepository.constructAndSave(any(), any(), any(), any(), any(), any())).thenAnswer(invocation -> {
            synchronized (messageLatch) {
                messageLatch.countDown();
                return null;
            }
        });

        var wrongEvent = getEvent();
        wrongEvent.getHeader().setOperationTimeStamp("1234");
        var message = MessageBuilder
                .withPayload(toJson(wrongEvent))
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
        kafkaTemplate.send(message);
        kafkaTemplate.flush();

        then(messageLatch.await(60, TimeUnit.SECONDS)).isTrue();

        synchronized (messageLatch) {
            verify(messageRepository, times(0)).save(any());
            var responseCaptor = ArgumentCaptor.forClass(String.class);
            verify(outMessageRepository).constructAndSave(any(), responseCaptor.capture(), any(), any(), any(), any());
            var responses = responseCaptor.getAllValues();
            then(responses).hasSize(1);
            then(responses.get(0)).contains("operationTimeStamp: does not match the regex pattern");
            then(responses.get(0)).contains("\"msgCode\":10.00");
        }
    }
}
