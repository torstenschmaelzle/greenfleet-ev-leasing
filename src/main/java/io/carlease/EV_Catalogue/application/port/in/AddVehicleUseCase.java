package io.carlease.ev_catalogue.application.port.in;

import io.carlease.ev_catalogue.domain.*;
import io.carlease.shared.domain.*;

public interface AddVehicleUseCase {

    void add(VehicleId vehicleId,
             VehicleSpecification specification,
             BatterySpecification batterySpecification,
             Performance performance,
             Amount monthlyLeasePrice,
             Percentage initialStateOfHealth,
             Range initialMeasuredRangeKm);
}
