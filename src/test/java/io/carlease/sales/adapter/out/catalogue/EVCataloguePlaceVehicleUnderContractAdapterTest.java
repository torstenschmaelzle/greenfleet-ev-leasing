package io.carlease.sales.adapter.out.catalogue;

import io.carlease.EV_Catalogue.application.port.in.assignVehicleToContract;
import io.carlease.EV_Catalogue.domain.VehicleId;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

class EVCataloguePlaceVehicleUnderContractAdapterTest {

    private final assignVehicleToContract assignUseCase = mock(assignVehicleToContract.class);
    private final EVCataloguePlaceVehicleUnderContractAdapter adapter = new EVCataloguePlaceVehicleUnderContractAdapter(assignUseCase);

    @Test
    void placeUnderContract_callsAssignWithCorrectVehicleId() {
        willDoNothing().given(assignUseCase).assign(VehicleId.of("WBA00000000000101"));

        adapter.placeUnderContract("WBA00000000000101");

        then(assignUseCase).should().assign(VehicleId.of("WBA00000000000101"));
    }
}
