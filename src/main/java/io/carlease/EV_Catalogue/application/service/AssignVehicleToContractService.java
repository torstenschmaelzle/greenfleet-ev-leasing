package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.assignVehicleToContract;
import io.carlease.EV_Catalogue.application.port.out.VehicleBroadcastPort;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;
import io.carlease.EV_Catalogue.domain.VehicleNotFoundException;
import io.carlease.EV_Catalogue.domain.VehicleStatus;
import org.springframework.stereotype.Service;

@Service
public class AssignVehicleToContractService implements assignVehicleToContract {

    private final VehiclePersistancePort vehiclePersistancePort;
    private final VehicleBroadcastPort broadcaster;

    public AssignVehicleToContractService(VehiclePersistancePort vehiclePersistancePort, VehicleBroadcastPort broadcaster) {
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
