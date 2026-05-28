package io.carlease.EV_Catalogue.application.port.in;

import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;

public interface GetVehicleUseCase {
    Vehicle getByVin(VehicleId vehicleId);
}
