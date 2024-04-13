package edu.kalinin.acc.log.appender.console;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import edu.kalinin.acc.event.BusinessEvent;

@Component
@Log4j2
public class ConsoleAppender implements ApplicationListener<BusinessEvent> {
    @Override
    public void onApplicationEvent(BusinessEvent businessEvent) {
        switch (businessEvent.getLoglevel()) {
            case INFO: {
                log.info(businessEvent.getMessage());
                break;
            }
            case WARN: {
                log.warn(businessEvent.getMessage());
                break;
            }
            case DEBUG: {
                log.debug(businessEvent.getMessage());
                break;
            }
        }
    }
}
