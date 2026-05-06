package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.releaseVehicle;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;
import org.springframework.stereotype.Service;

@Service
public class ReleaseVehicleService implements releaseVehicle {

    private final VehiclePersistancePort vehiclePersistancePort;

    public ReleaseVehicleService(VehiclePersistancePort vehiclePersistancePort) {
        this.vehiclePersistancePort = vehiclePersistancePort;
    }

    @Override
    public void release(VehicleId vehicleId) {
        Vehicle vehicle = vehiclePersistancePort.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId.getVin()));
        vehicle.releaseLock();
        vehiclePersistancePort.save(vehicle);
    }
}
