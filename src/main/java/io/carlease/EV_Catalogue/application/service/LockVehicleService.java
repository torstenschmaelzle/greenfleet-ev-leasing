package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.in.LockVehicleUseCase;
import io.carlease.ev_catalogue.application.port.out.VehicleBroadcastPort;
import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.LockDuration;
import io.carlease.ev_catalogue.domain.Vehicle;
import io.carlease.ev_catalogue.domain.VehicleId;
import io.carlease.ev_catalogue.domain.VehicleNotFoundException;
import io.carlease.ev_catalogue.domain.VehicleStatus;
import org.springframework.stereotype.Service;

@Service
public class LockVehicleService implements LockVehicleUseCase {

    private final VehiclePersistencePort vehiclePersistancePort;
    private final VehicleBroadcastPort broadcaster;

    public LockVehicleService(VehiclePersistencePort vehiclePersistancePort, VehicleBroadcastPort broadcaster) {
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
