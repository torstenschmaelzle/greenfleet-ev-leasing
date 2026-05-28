package io.carlease.ev_catalogue.application.port.out;

import io.carlease.ev_catalogue.domain.Vehicle;
import io.carlease.ev_catalogue.domain.VehicleId;
import org.jmolecules.ddd.annotation.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiclePersistencePort {

    void save(Vehicle vehicle);

    boolean existsById(VehicleId vehicleId);

    Optional<Vehicle> findById(VehicleId vehicleId);

    void deleteById(VehicleId vehicleId);

    List<Vehicle> findAllReserved();
}
