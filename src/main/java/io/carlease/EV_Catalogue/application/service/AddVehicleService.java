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
            throw new IllegalStateException("Vehicle already exists in catalogue: " + vehicleId.getVin());
        }

        // NOTE: BatteryHealthRecord is package-private in the domain.
        // A VehicleFactory in the domain package is needed to resolve this.
        // e.g. VehicleFactory.create(vehicleId, specification, batterySpecification,
        //          performance, monthlyLeasePrice, initialStateOfHealth, initialMeasuredRangeKm)

        // TEMP: BatteryHealthRecord wurde temporär public gemacht.
        // Sobald VehicleFactory im Domain-Package existiert, rückgängig machen.

        //BatteryHealthRecord initialRecord = new BatteryHealthRecord(initialStateOfHealth, initialMeasuredRangeKm);
        Vehicle vehicle =  Vehicle.create(vehicleId, specification, batterySpecification,
                performance, monthlyLeasePrice, initialStateOfHealth, initialMeasuredRangeKm);

        persistVehiclePort.save(vehicle);
        logger.info("Vehicle added to EV catalogue: {}", vehicleId.getVin());
    }
}
