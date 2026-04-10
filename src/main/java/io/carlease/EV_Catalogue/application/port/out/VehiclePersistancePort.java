package io.carlease.EV_Catalogue.application.port.out;

import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;
import org.jmolecules.ddd.annotation.Repository;

import java.util.Optional;

@Repository
public interface VehiclePersistancePort {

    void save(Vehicle vehicle);

    boolean existsById(VehicleId vehicleId);

    Optional<Vehicle> findById(VehicleId vehicleId);

    void deleteById(VehicleId vehicleId);
}
