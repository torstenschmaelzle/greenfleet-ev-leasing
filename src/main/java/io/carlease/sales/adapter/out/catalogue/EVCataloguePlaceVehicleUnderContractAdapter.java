package io.carlease.sales.adapter.out.catalogue;

import io.carlease.EV_Catalogue.application.port.in.assignVehicleToContract;
import io.carlease.EV_Catalogue.domain.VehicleId;
import io.carlease.sales.application.port.out.PlaceVehicleUnderContractPort;
import org.springframework.stereotype.Component;

@Component
public class EVCataloguePlaceVehicleUnderContractAdapter implements PlaceVehicleUnderContractPort {

    private final assignVehicleToContract assignVehicleToContractUseCase;

    public EVCataloguePlaceVehicleUnderContractAdapter(assignVehicleToContract assignVehicleToContractUseCase) {
        this.assignVehicleToContractUseCase = assignVehicleToContractUseCase;
    }

    @Override
    public void placeUnderContract(String vin) {
        assignVehicleToContractUseCase.assign(VehicleId.of(vin));
    }
}
