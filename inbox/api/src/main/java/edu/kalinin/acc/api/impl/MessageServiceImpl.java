package edu.kalinin.acc.api.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.kalinin.acc.api.MessageService;
import edu.kalinin.acc.dto.MessageDto;
import edu.kalinin.acc.mapper.MessageMapper;
import edu.kalinin.acc.repository.MessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Transactional
    @Override
    public List<MessageDto> findAllOrderByCreated(String topic) {
        return messageMapper.toDto(messageRepository.findAllByTopicOrderByAttemptCountAscCreatedMomentAsc(topic));
    }

    @Transactional
    @Override
    public List<MessageDto> findOldOrderByCreated(String topic) {
        return messageMapper.toDto(messageRepository.findAllByTopicAndAttemptCountIsNotNullOrderByCreatedMomentAsc(topic));
    }

    @Override
    public void softDelete(MessageDto message) {
        messageRepository.deleteById(message.getId());
    }

    @Override
    public void toRetry(String messageId, String errorMsg) {
        messageRepository.findById(messageId)
                .ifPresent(message -> {
                    message.setAttemptCount(message.getAttemptCount() + 1);
                    message.setErrorMessage(errorMsg);
                    messageRepository.save(message);
                });
    }
}
