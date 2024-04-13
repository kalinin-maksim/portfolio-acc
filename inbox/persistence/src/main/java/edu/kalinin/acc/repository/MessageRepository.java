package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.kalinin.acc.entity.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findAllByTopicOrderByAttemptCountAscCreatedMomentAsc(String topic);

    List<Message> findAllByTopicAndAttemptCountIsNotNullOrderByCreatedMomentAsc(String topic);

    @Override
    default void deleteById(String messageId) {
        findById(messageId)
                .ifPresent(message -> {
                    message.setDeleted(true);
                    save(message);
                });
    }
}