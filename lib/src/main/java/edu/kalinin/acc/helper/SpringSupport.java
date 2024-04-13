package edu.kalinin.acc.helper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Getter
@Slf4j
public class SpringSupport {
    private static ApplicationContext context;

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringSupport(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    @Autowired
    @SuppressWarnings("java:S2696")
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringSupport.context = applicationContext;
    }

    public static ApplicationEventPublisher getApplicationEventPublisher(){
        return context.getBean(SpringSupport.class).applicationEventPublisher;
    }
}
