package edu.kalinin.acc.api;

import edu.kalinin.acc.dto.MessageDto;

import java.util.List;

public interface MessageService {
    List<MessageDto> findAllOrderByCreated(String topic);
    List<MessageDto> findOldOrderByCreated(String topic);



    void softDelete(MessageDto message);

    void toRetry(String messageId, String message);
}
