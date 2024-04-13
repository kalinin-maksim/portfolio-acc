package edu.kalinin.acc.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import edu.kalinin.acc.api.impl.MessageServiceImpl;
import edu.kalinin.acc.entity.Message;
import edu.kalinin.acc.mapper.MessageMapperImpl;
import edu.kalinin.acc.repository.MessageRepository;

import java.util.Collections;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                MessageRepository.class,
                MessageServiceImpl.class,
                MessageMapperImpl.class,
        }
)
class MessageServiceTest {

    @Autowired
    MessageService messageService;
    @MockBean
    MessageRepository messageRepository;

    @Test
    void deleteAllTest(){
        var topic = "";
        var message = new Message();
        Mockito.when(messageRepository.findAllByTopicOrderByAttemptCountAscCreatedMomentAsc(any())).thenReturn(Collections.singletonList(message));

        for (var messageDto : messageService.findAllOrderByCreated(topic)) {
            messageService.softDelete(messageDto);
        }

        then(messageRepository.findAllByTopicOrderByAttemptCountAscCreatedMomentAsc(topic)).hasSize(1);
    }
}