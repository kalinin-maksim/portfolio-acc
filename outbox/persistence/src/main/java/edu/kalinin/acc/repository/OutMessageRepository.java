package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.kalinin.acc.entity.OutMessage;
import edu.kalinin.acc.selector.Channel;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutMessageRepository extends JpaRepository<OutMessage, String> {
    List<OutMessage> findByDeletedFalseAndChannel(Channel channel);
    List<OutMessage> findAllByProcessIdAndDeletedTrue(String processId);

    default OutMessage constructAndSave(String message, String response, String level, Channel channel, String address, String processId) {
        var outMessage = new OutMessage();
        outMessage.setProcessId(processId);
        outMessage.setLevel(level);
        outMessage.setMessage(message);
        outMessage.setResponse(response);
        outMessage.setChannel(channel);
        outMessage.setAddress(address);
        outMessage.setCreatedMoment(LocalDateTime.now());
        return save(outMessage);
    }

}