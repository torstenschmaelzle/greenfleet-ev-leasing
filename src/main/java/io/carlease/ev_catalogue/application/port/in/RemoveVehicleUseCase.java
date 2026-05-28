package io.carlease.ev_catalogue.application.port.in;

import io.carlease.ev_catalogue.domain.VehicleId;

public interface RemoveVehicleUseCase {

    void remove(VehicleId vehicleId);
}
