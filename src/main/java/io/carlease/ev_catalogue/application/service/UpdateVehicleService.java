package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.in.UpdateVehicleUseCase;
import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.*;
import io.carlease.shared.domain.*;
import org.jmolecules.architecture.layered.ApplicationLayer;
import org.jmolecules.ddd.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ApplicationLayer
@Service
public class UpdateVehicleService implements UpdateVehicleUseCase {

    private final VehiclePersistencePort persistVehiclePort;
    private final Logger logger = LoggerFactory.getLogger(UpdateVehicleService.class);

    public UpdateVehicleService(VehiclePersistencePort persistVehiclePort) {
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
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        vehicle.updateVehicleSpecification(specification);
        vehicle.updateBatterySpecification(batterySpecification);
        vehicle.updateMonthlyLeasePrice(monthlyLeasePrice);
        vehicle.updateBatteryHealthMeasurement(newStateOfHealth, newMeasuredRangeKm);

        persistVehiclePort.save(vehicle);
        logger.info("Vehicle updated in EV catalogue: {}", vehicleId.getVin());
    }
}
