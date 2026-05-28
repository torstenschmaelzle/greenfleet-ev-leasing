package io.carlease.ev_catalogue.adapter.out.event;

import io.carlease.ev_catalogue.domain.VehicleLockExpiredEvent;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

class SpringVehicleEventAdapterTest {

    private final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
    private final SpringVehicleEventAdapter adapter = new SpringVehicleEventAdapter(publisher);

    @Test
    void publishLocksExpired_publishesCorrectEventType() {
        List<String> vins = List.of("WBA00000000000101", "WBA00000000000102");
        willDoNothing().given(publisher).publishEvent(new VehicleLockExpiredEvent(vins));

        adapter.publishLocksExpired(vins);

        then(publisher).should().publishEvent(new VehicleLockExpiredEvent(vins));
    }
}
