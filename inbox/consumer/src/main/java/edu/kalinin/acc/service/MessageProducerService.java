package edu.kalinin.acc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import edu.kalinin.acc.entity.Message;
import edu.kalinin.acc.repository.MessageRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class MessageProducerService {

    private final MessageRepository messageRepository;

    public void save(String topic, String msgId, String body) {
        var message = new Message();
        message.setTopic(topic);
        message.setMsgId(msgId);
        message.setBody(body);
        message.setDeleted(false);
        message.setCreatedMoment(LocalDateTime.now());
        messageRepository.save(message);
    }
}
