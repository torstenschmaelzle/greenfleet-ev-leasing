package io.carlease.sales.adapter.out.catalogue;

import io.carlease.EV_Catalogue.application.port.in.lockVehicle;
import io.carlease.EV_Catalogue.domain.LockDuration;
import io.carlease.EV_Catalogue.domain.VehicleId;
import io.carlease.sales.application.port.out.LockVehiclePort;
import org.springframework.stereotype.Component;

@Component
public class EVCatalogueLockVehicleAdapter implements LockVehiclePort {

    private final lockVehicle lockVehicleUseCase;

    public EVCatalogueLockVehicleAdapter(lockVehicle lockVehicleUseCase) {
        this.lockVehicleUseCase = lockVehicleUseCase;
    }

    @Override
    public void lock(String vin) {
        lockVehicleUseCase.lock(VehicleId.of(vin), LockDuration.defaultDuration());
    }
}
