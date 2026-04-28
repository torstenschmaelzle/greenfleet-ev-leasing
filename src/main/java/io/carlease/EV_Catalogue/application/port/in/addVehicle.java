package io.carlease.EV_Catalogue.application.port.in;

import io.carlease.EV_Catalogue.domain.*;
import io.carlease.shared.domain.*;

public interface addVehicle {

    void add(VehicleId vehicleId,
             VehicleSpecification specification,
             BatterySpecification batterySpecification,
             Performance performance,
             Amount monthlyLeasePrice,
             Percentage initialStateOfHealth,
             Range initialMeasuredRangeKm);
}
