package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.GetVehicleUseCase;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;
import io.carlease.EV_Catalogue.domain.VehicleNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetVehicleService implements GetVehicleUseCase {

    private final VehiclePersistancePort vehiclePersistancePort;

    public GetVehicleService(VehiclePersistancePort vehiclePersistancePort) {
        this.vehiclePersistancePort = vehiclePersistancePort;
    }

    @Override
    public Vehicle getByVin(VehicleId vehicleId) {
        return vehiclePersistancePort.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }
}
