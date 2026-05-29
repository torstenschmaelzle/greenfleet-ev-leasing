package io.carlease.sales.adapter.in.event;

import io.carlease.sales.application.port.in.OnVehicleLockExpiredUseCase;
import io.carlease.sales.domain.VehicleLocksExpiredEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

class VehicleLockExpiredListenerTest {

    private final OnVehicleLockExpiredUseCase useCase = mock(OnVehicleLockExpiredUseCase.class);
    private final VehicleLockExpiredListener listener = new VehicleLockExpiredListener(useCase);

    @Test
    void on_delegatesToUseCaseWithVins() {
        List<String> vins = List.of("WBA00000000000101", "WBA00000000000102");
        willDoNothing().given(useCase).handle(vins);

        listener.on(new VehicleLocksExpiredEvent(vins));

        then(useCase).should().handle(vins);
    }
}
