package edu.kalinin.acc.log.appender.db.h2;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import edu.kalinin.acc.entity.LogEntry;
import edu.kalinin.acc.event.BusinessEvent;
import edu.kalinin.acc.helper.SpringSupport;
import edu.kalinin.acc.repository.LogEntryRepository;

@Component
@Profile("local")
@Log4j2
public class H2Appender implements ApplicationListener<BusinessEvent> {

    @Transactional
    @Override
    public void onApplicationEvent(BusinessEvent businessEvent) {
        var logEntryRepository = SpringSupport.getContext().getBean(LogEntryRepository.class);

        var logEntry = new LogEntry();
        logEntry.setMessage(businessEvent.getMessage());
        logEntry.setLevel(businessEvent.getLoglevel().name());
        logEntryRepository.save(logEntry);
    }
}
