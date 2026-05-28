package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.in.GetVehicleUseCase;
import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.Vehicle;
import io.carlease.ev_catalogue.domain.VehicleId;
import io.carlease.ev_catalogue.domain.VehicleNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetVehicleService implements GetVehicleUseCase {

    private final VehiclePersistencePort vehiclePersistancePort;

    public GetVehicleService(VehiclePersistencePort vehiclePersistancePort) {
        this.vehiclePersistancePort = vehiclePersistancePort;
    }

    @Override
    public Vehicle getByVin(VehicleId vehicleId) {
        return vehiclePersistancePort.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }
}
