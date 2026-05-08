package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.addVehicle;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.*;
import io.carlease.shared.domain.*;
import org.jmolecules.architecture.layered.ApplicationLayer;
import org.jmolecules.ddd.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ApplicationLayer
@Service
public class AddVehicleService implements addVehicle {

    private final VehiclePersistancePort persistVehiclePort;
    private final Logger logger = LoggerFactory.getLogger(AddVehicleService.class);

    public AddVehicleService(VehiclePersistancePort persistVehiclePort) {
        this.persistVehiclePort = persistVehiclePort;
    }

    @Override
    public void add(VehicleId vehicleId,
                    VehicleSpecification specification,
                    BatterySpecification batterySpecification,
                    Performance performance,
                    Amount monthlyLeasePrice,
                    Percentage initialStateOfHealth,
                    Range initialMeasuredRangeKm) {

        if (persistVehiclePort.existsById(vehicleId)) {
            logger.warn("Vehicle already exists in catalogue: {}", vehicleId.getVin());
            throw new DuplicateVehicleVinException(vehicleId.getVin());
        }

        Vehicle vehicle = VehicleFactory.createVehicle(vehicleId, specification, batterySpecification,
                performance, monthlyLeasePrice, initialStateOfHealth, initialMeasuredRangeKm);

        persistVehiclePort.save(vehicle);
        logger.info("Vehicle added to EV catalogue: {}", vehicleId.getVin());
    }
}
