package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.in.RemoveVehicleUseCase;
import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.Vehicle;
import io.carlease.ev_catalogue.domain.VehicleId;
import io.carlease.ev_catalogue.domain.VehicleNotFoundException;
import org.jmolecules.architecture.layered.ApplicationLayer;
import org.jmolecules.ddd.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ApplicationLayer
@Service
public class RemoveVehicleService implements RemoveVehicleUseCase {

    private final VehiclePersistencePort persistVehiclePort;
    private final Logger logger = LoggerFactory.getLogger(RemoveVehicleService.class);

    public RemoveVehicleService(VehiclePersistencePort persistVehiclePort) {
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
