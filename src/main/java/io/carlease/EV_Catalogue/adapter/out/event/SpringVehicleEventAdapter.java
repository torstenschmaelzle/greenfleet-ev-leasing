package io.carlease.ev_catalogue.adapter.out.event;

import io.carlease.ev_catalogue.application.port.out.VehicleEventPort;
import io.carlease.ev_catalogue.domain.VehicleLockExpiredEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpringVehicleEventAdapter implements VehicleEventPort {

    private final ApplicationEventPublisher publisher;

    public SpringVehicleEventAdapter(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publishLocksExpired(List<String> vins) {
        publisher.publishEvent(new VehicleLockExpiredEvent(vins));
    }
}
