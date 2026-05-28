package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.in.PlaceVehicleUnderContractUseCase;
import io.carlease.ev_catalogue.application.port.out.VehicleBroadcastPort;
import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.Vehicle;
import io.carlease.ev_catalogue.domain.VehicleId;
import io.carlease.ev_catalogue.domain.VehicleNotFoundException;
import io.carlease.ev_catalogue.domain.VehicleStatus;
import org.springframework.stereotype.Service;

@Service
public class PlaceVehicleUnderContractService implements PlaceVehicleUnderContractUseCase {

    private final VehiclePersistencePort vehiclePersistancePort;
    private final VehicleBroadcastPort broadcaster;

    public PlaceVehicleUnderContractService(VehiclePersistencePort vehiclePersistancePort, VehicleBroadcastPort broadcaster) {
        this.vehiclePersistancePort = vehiclePersistancePort;
        this.broadcaster = broadcaster;
    }

    @Override
    public void assign(VehicleId vehicleId) {
        Vehicle vehicle = vehiclePersistancePort.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
        vehicle.assignToContract();
        vehiclePersistancePort.save(vehicle);
        broadcaster.broadcast(vehicleId.getVin(), VehicleStatus.UNDER_CONTRACT);
    }
}
