package io.carlease.sales.adapter.out.catalogue;

import io.carlease.ev_catalogue.application.port.in.LockVehicleUseCase;
import io.carlease.ev_catalogue.domain.LockDuration;
import io.carlease.ev_catalogue.domain.VehicleId;
import io.carlease.ev_catalogue.domain.VehicleNotAvailableException;
import io.carlease.sales.application.port.out.ReserveVehiclePort;
import io.carlease.sales.domain.ReservationExpiredException;
import org.springframework.stereotype.Component;

@Component
public class EVCatalogueReserveVehicleAdapter implements ReserveVehiclePort {

    private final LockVehicleUseCase lockVehicleUseCase;

    public EVCatalogueReserveVehicleAdapter(LockVehicleUseCase lockVehicleUseCase) {
        this.lockVehicleUseCase = lockVehicleUseCase;
    }

    @Override
    public void reserve(String vin) {
        try {
            lockVehicleUseCase.lock(VehicleId.of(vin), LockDuration.defaultDuration());
        } catch (VehicleNotAvailableException e) {
            throw new ReservationExpiredException(vin);
        }
    }
}
