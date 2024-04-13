package edu.kalinin.acc.consumer;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import edu.kalinin.acc.ApplicationInbox;
import edu.kalinin.acc.repository.MessageRepository;
import edu.kalinin.acc.repository.OutMessageRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static edu.kalinin.acc.helper.TestDataFactory.EventFactory.getEvent;

@ContextConfiguration(classes = {ApplicationInbox.class})
@SpringBootTest
@ActiveProfiles({
        "kafka-local",
        "kafka-consumer-test"
})
@DirtiesContext
@TestPropertySource(
        properties = {
                "spring.embedded.kafka.brokers=localhost:9092"
        })
class DoubleMessageTest {

    @Autowired
    private KafKaEventConsumer kafKaEventConsumer;

    @Autowired
    private MessageRepository messageRepository;

    @MockBean
    private OutMessageRepository outMessageRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    @SneakyThrows
    void doubleMessageTest() {

        var messageLatch = new CountDownLatch(1);

        when(outMessageRepository.constructAndSave(any(), any(), any(), any(), any(), any())).thenAnswer(invocation -> {
            synchronized (messageLatch) {
                messageLatch.countDown();
                return null;
            }
        });

        kafKaEventConsumer.receive(getEvent());
        kafKaEventConsumer.receive(getEvent());

        then(messageLatch.await(60, TimeUnit.SECONDS)).isTrue();

        synchronized (messageLatch) {
            then(messageRepository.findAll()).hasSize(1);
            var responseCaptor = ArgumentCaptor.forClass(String.class);
            verify(outMessageRepository, times(2)).constructAndSave(any(), responseCaptor.capture(), any(), any(), any(), any());
            var responses = responseCaptor.getAllValues();
            then(responses).hasSize(2);
            then(responses).anyMatch(s -> s.contains("double"));
        }
    }
}
