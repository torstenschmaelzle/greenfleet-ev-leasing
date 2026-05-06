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
    private final VehicleStatusBroadcaster broadcaster;

    public LockVehicleService(VehiclePersistancePort vehiclePersistancePort, VehicleStatusBroadcaster broadcaster) {
        this.vehiclePersistancePort = vehiclePersistancePort;
        this.broadcaster = broadcaster;
    }

    @Override
    public void lock(VehicleId vehicleId, LockDuration duration) {
        Vehicle vehicle = vehiclePersistancePort.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId.getVin()));
        vehicle.lockVehicle(duration);
        vehiclePersistancePort.save(vehicle);
        broadcaster.broadcast(vehicleId.getVin(), "RESERVED");
    }
}
