package io.carlease.sales.adapter.out.catalogue;

import io.carlease.EV_Catalogue.application.port.in.lockVehicle;
import io.carlease.EV_Catalogue.domain.LockDuration;
import io.carlease.EV_Catalogue.domain.VehicleId;
import io.carlease.sales.application.port.out.ReserveVehiclePort;
import org.springframework.stereotype.Component;

@Component
public class EVCatalogueReserveVehicleAdapter implements ReserveVehiclePort {

    private final lockVehicle lockVehicleUseCase;

    public EVCatalogueReserveVehicleAdapter(lockVehicle lockVehicleUseCase) {
        this.lockVehicleUseCase = lockVehicleUseCase;
    }

    @Override
    public void reserve(String vin) {
        lockVehicleUseCase.lock(VehicleId.of(vin), LockDuration.defaultDuration());
    }
}
