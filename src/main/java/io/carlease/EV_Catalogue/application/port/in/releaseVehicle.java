package io.carlease.EV_Catalogue.application.port.in;

import io.carlease.EV_Catalogue.domain.VehicleId;

public interface releaseVehicle {
    void release(VehicleId vehicleId);
}
