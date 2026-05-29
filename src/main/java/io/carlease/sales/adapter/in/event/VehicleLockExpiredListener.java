package io.carlease.sales.adapter.in.event;

import io.carlease.sales.application.port.in.OnVehicleLockExpiredUseCase;
import io.carlease.sales.domain.VehicleLocksExpiredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class VehicleLockExpiredListener {

    private final OnVehicleLockExpiredUseCase onVehicleLockExpiredUseCase;

    public VehicleLockExpiredListener(OnVehicleLockExpiredUseCase onVehicleLockExpiredUseCase) {
        this.onVehicleLockExpiredUseCase = onVehicleLockExpiredUseCase;
    }

    @EventListener
    public void on(VehicleLocksExpiredEvent event) {
        onVehicleLockExpiredUseCase.handle(event.vins());
    }
}
