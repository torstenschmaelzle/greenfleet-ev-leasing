package io.carlease.fleet_consulting.application.port.out;

import io.carlease.fleet_consulting.domain.VehicleSummary;

import java.util.List;

public interface GetVehiclesPort {
    List<VehicleSummary> getVehicles();
}
