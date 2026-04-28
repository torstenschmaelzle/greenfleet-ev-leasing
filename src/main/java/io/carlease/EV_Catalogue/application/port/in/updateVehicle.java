package io.carlease.EV_Catalogue.application.port.in;

import io.carlease.EV_Catalogue.domain.*;
import io.carlease.shared.domain.*;

public interface updateVehicle {

    void update(VehicleId vehicleId,
                VehicleSpecification specification,
                BatterySpecification batterySpecification,
                Amount monthlyLeasePrice,
                Percentage newStateOfHealth,
                Range newMeasuredRangeKm);
}
