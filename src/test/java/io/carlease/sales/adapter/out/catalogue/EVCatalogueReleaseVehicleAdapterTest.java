package io.carlease.sales.adapter.out.catalogue;

import io.carlease.EV_Catalogue.application.port.in.releaseVehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

class EVCatalogueReleaseVehicleAdapterTest {

    private final releaseVehicle releaseUseCase = mock(releaseVehicle.class);
    private final EVCatalogueReleaseVehicleAdapter adapter = new EVCatalogueReleaseVehicleAdapter(releaseUseCase);

    @Test
    void release_callsReleaseWithCorrectVehicleId() {
        willDoNothing().given(releaseUseCase).release(VehicleId.of("WBA00000000000101"));

        adapter.release("WBA00000000000101");

        then(releaseUseCase).should().release(VehicleId.of("WBA00000000000101"));
    }
}
