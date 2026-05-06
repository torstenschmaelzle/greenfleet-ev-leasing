package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.lockVehicle;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.LockDuration;
import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;
import org.springframework.stereotype.Service;

@Service
public class LockVehicleService implements lockVehicle {

    private final VehiclePersistancePort vehiclePersistancePort;

    public LockVehicleService(VehiclePersistancePort vehiclePersistancePort) {
        this.vehiclePersistancePort = vehiclePersistancePort;
    }

    @Override
    public void lock(VehicleId vehicleId, LockDuration duration) {
        Vehicle vehicle = vehiclePersistancePort.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId.getVin()));
        vehicle.lockVehicle(duration);
        vehiclePersistancePort.save(vehicle);
    }
}
