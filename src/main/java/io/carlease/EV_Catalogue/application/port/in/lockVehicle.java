package io.carlease.EV_Catalogue.application.port.in;

import io.carlease.EV_Catalogue.domain.LockDuration;
import io.carlease.EV_Catalogue.domain.VehicleId;

public interface lockVehicle {
    void lock(VehicleId vehicleId, LockDuration duration);
}
