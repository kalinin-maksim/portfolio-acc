package edu.kalinin.acc.log.appender.outbox;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import edu.kalinin.acc.event.BusinessEvent;
import edu.kalinin.acc.helper.SpringSupport;
import edu.kalinin.acc.repository.OutMessageRepository;

@Component
@Log4j2
public class OutboxAppender implements ApplicationListener<BusinessEvent> {
    @Override
    public void onApplicationEvent(BusinessEvent businessEvent) {
        var outMessageRepository = SpringSupport.getContext().getBean(OutMessageRepository.class);

        outMessageRepository.constructAndSave(
                businessEvent.getMessage(),
                businessEvent.getResponseJson(),
                businessEvent.getLoglevel().name(),
                businessEvent.getChannel(),
                businessEvent.getAddress(),
                businessEvent.getProcessId());
    }
}