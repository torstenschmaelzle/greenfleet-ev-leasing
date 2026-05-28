package io.carlease.sales.adapter.out.catalogue;

import io.carlease.ev_catalogue.application.port.in.PlaceVehicleUnderContractUseCase;
import io.carlease.ev_catalogue.domain.VehicleUnderContractViolationException;
import io.carlease.ev_catalogue.domain.VehicleId;
import io.carlease.sales.application.port.out.PlaceVehicleUnderContractPort;
import io.carlease.sales.domain.ReservationExpiredException;
import org.springframework.stereotype.Component;

@Component
public class EVCataloguePlaceVehicleUnderContractAdapter implements PlaceVehicleUnderContractPort {

    private final PlaceVehicleUnderContractUseCase placeVehicleUnderContractUseCase;

    public EVCataloguePlaceVehicleUnderContractAdapter(PlaceVehicleUnderContractUseCase placeVehicleUnderContractUseCase) {
        this.placeVehicleUnderContractUseCase = placeVehicleUnderContractUseCase;
    }

    @Override
    public void placeUnderContract(String vin) {
        try {
            placeVehicleUnderContractUseCase.assign(VehicleId.of(vin));
        } catch (VehicleUnderContractViolationException e) {
            throw new ReservationExpiredException(vin);
        }
    }
}
