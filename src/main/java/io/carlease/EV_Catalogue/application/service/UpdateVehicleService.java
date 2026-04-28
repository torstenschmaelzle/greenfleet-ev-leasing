package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.updateVehicle;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.*;
import io.carlease.shared.domain.*;
import org.jmolecules.architecture.layered.ApplicationLayer;
import org.jmolecules.ddd.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ApplicationLayer
@Service
public class UpdateVehicleService implements updateVehicle {

    private final VehiclePersistancePort persistVehiclePort;
    private final Logger logger = LoggerFactory.getLogger(UpdateVehicleService.class);

    public UpdateVehicleService(VehiclePersistancePort persistVehiclePort) {
        this.persistVehiclePort = persistVehiclePort;
    }

    @Override
    public void update(VehicleId vehicleId,
                       VehicleSpecification specification,
                       BatterySpecification batterySpecification,
                       Amount monthlyLeasePrice,
                       Percentage newStateOfHealth,
                       Range newMeasuredRangeKm) {

        Vehicle vehicle = persistVehiclePort.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId.getVin()));

        vehicle.updateVehicleSpecification(specification);
        vehicle.updateBatterySpecification(batterySpecification);
        vehicle.updateMonthlyLeasePrice(monthlyLeasePrice);
        vehicle.updateBatteryHealthMeasurement(newStateOfHealth, newMeasuredRangeKm);

        persistVehiclePort.save(vehicle);
        logger.info("Vehicle updated in EV catalogue: {}", vehicleId.getVin());
    }
}
