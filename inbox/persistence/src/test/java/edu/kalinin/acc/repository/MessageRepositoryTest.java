package edu.kalinin.acc.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import edu.kalinin.acc.entity.Message;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EntityScan(basePackageClasses = Message.class)
@EnableJpaRepositories(basePackageClasses = MessageRepository.class)
@ContextConfiguration(classes = {
        MessageRepository.class
})
public class MessageRepositoryTest {
    public static final UUID ZERO_UUID = java.util.UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Autowired
    private MessageRepository messageRepository;

    @Test
    void saveTest() {
        var message = new Message();
        message.setId(ZERO_UUID.toString());
        var messageSaved = messageRepository.save(message);

        assertThat(messageSaved.getId(), org.hamcrest.Matchers.notNullValue());
    }
}