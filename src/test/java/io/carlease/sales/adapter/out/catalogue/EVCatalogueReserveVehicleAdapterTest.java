package io.carlease.sales.adapter.out.catalogue;

import io.carlease.ev_catalogue.application.port.in.LockVehicleUseCase;
import io.carlease.ev_catalogue.domain.LockDuration;
import io.carlease.ev_catalogue.domain.VehicleId;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

class EVCatalogueReserveVehicleAdapterTest {

    private final LockVehicleUseCase lockUseCase = mock(LockVehicleUseCase.class);
    private final EVCatalogueReserveVehicleAdapter adapter = new EVCatalogueReserveVehicleAdapter(lockUseCase);

    @Test
    void reserve_callsLockWithCorrectVehicleIdAndDefaultDuration() {
        willDoNothing().given(lockUseCase).lock(VehicleId.of("WBA00000000000101"), LockDuration.defaultDuration());

        adapter.reserve("WBA00000000000101");

        then(lockUseCase).should().lock(VehicleId.of("WBA00000000000101"), LockDuration.defaultDuration());
    }
}
