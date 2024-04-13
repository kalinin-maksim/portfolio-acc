package edu.kalinin.acc.sheduled;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import edu.kalinin.acc.service.OutMessageSenderService;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OutMessageSenderScheduler {

    private final OutMessageSenderService outMessageSenderService;

    @Scheduled(fixedRateString = "${application.outbox.job.rate-sec}", timeUnit = TimeUnit.SECONDS)
    @SchedulerLock(name = "outMessageSendViaKafkaJob")
    public void outMessageSendViaKafkaJob() {
        outMessageSenderService.send();
    }
}