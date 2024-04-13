package edu.kalinin.acc.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import edu.kalinin.acc.configuration.IntegrationConfiguration;
import edu.kalinin.acc.helper.SpringSupport;
import edu.kalinin.acc.log.appender.outbox.OutboxAppender;
import edu.kalinin.acc.repository.OutMessageRepository;
import edu.kalinin.acc.service.ResponseCodeService;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        SpringSupport.class,
        MessageEventFabric.class,
        OutboxAppender.class
})
class MessageEventFabricTest {

    @Autowired
    MessageEventFabric messageEventFabric;

    @MockBean
    IntegrationConfiguration integrationConfiguration;

    @MockBean
    OutMessageRepository outMessageRepository;

    @MockBean
    ResponseCodeService responseCodeService;

    @Test
    void observeTest() {

        when(integrationConfiguration.getTopicOut(any())).thenReturn("");
        MessageEventFabric.observe("", "", () -> {
        });
        verify(outMessageRepository, times(1)).constructAndSave(any(), any(), any(), any(), any(), any());
    }

    @Test
    void observeTest1() {

        when(integrationConfiguration.getTopicOut(any())).thenReturn("");
        MessageEventFabric.observe("", "", () -> { throw new DataIntegrityViolationException("");
        });
        verify(outMessageRepository, times(1)).constructAndSave(any(), any(), any(), any(), any(), any());
    }

    @Test
    void observeTest2() {

        when(integrationConfiguration.getTopicOut(any())).thenReturn("");
        MessageEventFabric.observe("", "", () -> { throw new RuntimeException("");
        });
        verify(outMessageRepository, times(1)).constructAndSave(any(), any(), any(), any(), any(), any());
    }

    @Test
    void observeTest3() {

        when(integrationConfiguration.getTopicOut(any())).thenReturn("");
        MessageEventFabric.createInvalidatedMessageEvent("\"accountingId\":\"1\", \"moduleId\":\"1\", \"reverse\":\"false\"", "");
        verify(outMessageRepository, times(1)).constructAndSave(any(), any(), any(), any(), any(), any());
    }

}