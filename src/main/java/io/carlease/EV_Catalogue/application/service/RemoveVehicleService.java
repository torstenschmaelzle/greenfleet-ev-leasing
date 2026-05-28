package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.removeVehicle;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;
import io.carlease.EV_Catalogue.domain.VehicleNotFoundException;
import org.jmolecules.architecture.layered.ApplicationLayer;
import org.jmolecules.ddd.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ApplicationLayer
@Service
public class RemoveVehicleService implements removeVehicle {

    private final VehiclePersistancePort persistVehiclePort;
    private final Logger logger = LoggerFactory.getLogger(RemoveVehicleService.class);

    public RemoveVehicleService(VehiclePersistancePort persistVehiclePort) {
        this.persistVehiclePort = persistVehiclePort;
    }

    @Override
    public void remove(VehicleId vehicleId) {
        Vehicle vehicle = persistVehiclePort.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        vehicle.remove();

        persistVehiclePort.deleteById(vehicleId);
        logger.info("Vehicle removed from EV catalogue: {}", vehicleId.getVin());
    }
}
