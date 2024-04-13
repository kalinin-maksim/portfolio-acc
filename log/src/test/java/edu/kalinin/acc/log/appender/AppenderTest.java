package edu.kalinin.acc.log.appender;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import edu.kalinin.acc.event.BusinessEvent;
import edu.kalinin.acc.log.appender.console.ConsoleAppender;
import edu.kalinin.acc.selector.Channel;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static edu.kalinin.acc.event.Level.INFO;

class AppenderTest {

    @Test
    void onApplicationEvent() {
        {
            var consoleAppender = new ConsoleAppender();
            consoleAppender.onApplicationEvent(businessEvent(Collections.emptyList()));
            assertThat(consoleAppender, Matchers.notNullValue());
        }
        {
            var consoleAppender = new ConsoleAppender();
            consoleAppender.onApplicationEvent(businessEvent(Collections.emptyList()));
            assertThat(consoleAppender, Matchers.notNullValue());
        }
        {
            var consoleAppender = new ConsoleAppender();
            consoleAppender.onApplicationEvent(businessEvent(Collections.emptyList()));
            assertThat(consoleAppender, Matchers.notNullValue());
        }
    }

    private BusinessEvent businessEvent(Object... attr) {
        return new BusinessEvent(INFO, Channel.KAFKA, "", "", "", "");
    }
}