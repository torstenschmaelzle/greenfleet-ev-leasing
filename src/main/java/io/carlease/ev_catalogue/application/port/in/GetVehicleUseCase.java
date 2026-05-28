package io.carlease.ev_catalogue.application.port.in;

import io.carlease.ev_catalogue.domain.Vehicle;
import io.carlease.ev_catalogue.domain.VehicleId;

public interface GetVehicleUseCase {
    Vehicle getByVin(VehicleId vehicleId);
}
