package io.carlease.sales.adapter.in.event;

import io.carlease.ev_catalogue.domain.VehicleLockExpiredEvent;
import io.carlease.sales.domain.VehicleLocksExpiredEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class VehicleLockExpiredTranslator {

    private final ApplicationEventPublisher publisher;

    public VehicleLockExpiredTranslator(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @EventListener
    public void on(VehicleLockExpiredEvent event) {
        publisher.publishEvent(new VehicleLocksExpiredEvent(event.vins()));
    }
}
