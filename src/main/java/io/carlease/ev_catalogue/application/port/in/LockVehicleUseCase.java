package io.carlease.ev_catalogue.application.port.in;

import io.carlease.ev_catalogue.domain.LockDuration;
import io.carlease.ev_catalogue.domain.VehicleId;

public interface LockVehicleUseCase {
    void lock(VehicleId vehicleId, LockDuration duration);
}
