package edu.kalinin.acc.health;

import org.springframework.boot.actuate.availability.ReadinessStateHealthIndicator;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextStateHealthIndicator extends ReadinessStateHealthIndicator {

    private static boolean contextInitialized = false;

    public ApplicationContextStateHealthIndicator(ApplicationAvailability availability) {
        super(availability);
    }

    protected AvailabilityState getState(ApplicationAvailability applicationAvailability) {
        return contextInitialized ? ReadinessState.ACCEPTING_TRAFFIC : ReadinessState.REFUSING_TRAFFIC;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void markAsUp() {
        contextInitialized = true;
    }

    @EventListener(ContextClosedEvent.class)
    public void markAsDown() {
        contextInitialized = false;
    }
}