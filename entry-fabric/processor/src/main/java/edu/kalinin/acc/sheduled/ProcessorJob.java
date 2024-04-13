package edu.kalinin.acc.sheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.provider.zookeeper.curator.ZookeeperCuratorLockProvider;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import edu.kalinin.acc.service.ProcessorService;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Log4j2
public class ProcessorJob {

    private final ProcessorService processorService;

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource);
    }

    @Bean
    public LockProvider lockProvider(org.apache.curator.framework.CuratorFramework client) {
        return new ZookeeperCuratorLockProvider(client);
    }

    @Scheduled(fixedRateString = "${application.processor.rate-sec}", timeUnit = TimeUnit.SECONDS)
    @SchedulerLock(name = "ProcessorJob_process")
    public void process() {
        processorService.processNewMessages();
    }

    @Scheduled(fixedRateString = "${application.processor.old-message.rate-sec}", timeUnit = TimeUnit.SECONDS)
    @SchedulerLock(name = "ProcessorJob_processOldMessages")
    public void processOldMessages() {
        processorService.processOldMessages();
    }
}