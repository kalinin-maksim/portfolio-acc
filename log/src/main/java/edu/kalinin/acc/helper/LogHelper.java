package edu.kalinin.acc.helper;

import lombok.experimental.UtilityClass;
import org.springframework.context.ApplicationEvent;

import static edu.kalinin.acc.helper.SpringSupport.getApplicationEventPublisher;

@UtilityClass
public class LogHelper {
    public static void log(ApplicationEvent event) {
        getApplicationEventPublisher().publishEvent(event);
    }
}
