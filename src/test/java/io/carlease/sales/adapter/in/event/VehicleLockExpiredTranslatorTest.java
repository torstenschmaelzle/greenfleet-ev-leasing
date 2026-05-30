package io.carlease.sales.adapter.in.event;

import io.carlease.ev_catalogue.domain.VehicleLockExpiredEvent;
import io.carlease.sales.domain.VehicleLocksExpiredEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class VehicleLockExpiredTranslatorTest {

    private final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
    private final VehicleLockExpiredTranslator translator = new VehicleLockExpiredTranslator(publisher);

    @Test
    void on_translatesAndPublishesSalesDomainEvent() {
        List<String> vins = List.of("WBA00000000000101", "WBA00000000000102");

        translator.on(new VehicleLockExpiredEvent(vins));

        ArgumentCaptor<VehicleLocksExpiredEvent> captor = ArgumentCaptor.forClass(VehicleLocksExpiredEvent.class);
        then(publisher).should().publishEvent(captor.capture());
        assertThat(captor.getValue().vins()).isEqualTo(vins);
    }
}
