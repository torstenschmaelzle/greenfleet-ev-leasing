package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.lockVehicle;
import io.carlease.EV_Catalogue.application.port.out.VehicleBroadcastPort;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.LockDuration;
import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;
import io.carlease.EV_Catalogue.domain.VehicleNotFoundException;
import io.carlease.EV_Catalogue.domain.VehicleStatus;
import org.springframework.stereotype.Service;

@Service
public class LockVehicleService implements lockVehicle {

    private final VehiclePersistancePort vehiclePersistancePort;
    private final VehicleBroadcastPort broadcaster;

    public LockVehicleService(VehiclePersistancePort vehiclePersistancePort, VehicleBroadcastPort broadcaster) {
        this.vehiclePersistancePort = vehiclePersistancePort;
        this.broadcaster = broadcaster;
    }

    @Override
    public void lock(VehicleId vehicleId, LockDuration duration) {
        Vehicle vehicle = vehiclePersistancePort.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
        vehicle.lockVehicle(duration);
        vehiclePersistancePort.save(vehicle);
        broadcaster.broadcast(vehicleId.getVin(), VehicleStatus.RESERVED);
    }
}
