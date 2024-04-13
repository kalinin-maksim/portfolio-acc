package edu.kalinin.acc.setvice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import edu.kalinin.acc.entity.OutMessage;
import edu.kalinin.acc.repository.OutMessageRepository;
import edu.kalinin.acc.selector.Channel;
import edu.kalinin.acc.service.OutMessageSenderService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EntityScan(basePackageClasses = OutMessage.class)
@EnableJpaRepositories(basePackageClasses = OutMessageRepository.class)
@ContextConfiguration(classes = {
        OutMessageSenderService.class
})
class OutMessageSenderServiceTest {

    @Autowired
    @InjectMocks
    OutMessageSenderService outMessageSenderService;

    @Autowired
    OutMessageRepository outMessageRepository;

    @MockBean
    KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void senMessageChain() {
        constructAndSave("Message received");
        outMessageSenderService.send();

        constructAndSave("double");
        outMessageSenderService.send();

        var kafkaMsgCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate, times(2)).send(kafkaMsgCaptor.capture());

        var kafkaMsgList = kafkaMsgCaptor.getAllValues();
        var kafkaMsgByContent = kafkaMsgList.stream()
                .collect(Collectors.groupingBy(Function.identity()));
        for (Map.Entry<Message, List<Message>> entry : kafkaMsgByContent.entrySet()) {
            if (entry.getKey().equals("created")) then(entry.getValue()).hasSize(2);
            if (entry.getKey().equals("double")) then(entry.getValue()).hasSize(0);
        }

    }

    private void constructAndSave(String message) {
        outMessageRepository.constructAndSave(message,"", "", Channel.KAFKA, "", "");
    }
}