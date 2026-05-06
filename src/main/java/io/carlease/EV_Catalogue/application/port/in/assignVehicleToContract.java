package io.carlease.EV_Catalogue.application.port.in;

import io.carlease.EV_Catalogue.domain.VehicleId;

public interface assignVehicleToContract {
    void assign(VehicleId vehicleId);
}
