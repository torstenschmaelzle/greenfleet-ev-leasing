package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.assignVehicleToContract;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;
import org.springframework.stereotype.Service;

@Service
public class AssignVehicleToContractService implements assignVehicleToContract {

    private final VehiclePersistancePort vehiclePersistancePort;

    public AssignVehicleToContractService(VehiclePersistancePort vehiclePersistancePort) {
        this.vehiclePersistancePort = vehiclePersistancePort;
    }

    @Override
    public void assign(VehicleId vehicleId) {
        Vehicle vehicle = vehiclePersistancePort.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId.getVin()));
        vehicle.assignToContract();
        vehiclePersistancePort.save(vehicle);
    }
}
