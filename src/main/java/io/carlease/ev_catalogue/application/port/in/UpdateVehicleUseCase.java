package io.carlease.ev_catalogue.application.port.in;

import io.carlease.ev_catalogue.domain.*;
import io.carlease.shared.domain.*;

public interface UpdateVehicleUseCase {

    void update(VehicleId vehicleId,
                VehicleSpecification specification,
                BatterySpecification batterySpecification,
                Amount monthlyLeasePrice,
                Percentage newStateOfHealth,
                Range newMeasuredRangeKm);
}
