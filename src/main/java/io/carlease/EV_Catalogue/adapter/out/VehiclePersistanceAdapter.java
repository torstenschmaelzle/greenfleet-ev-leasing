package io.carlease.EV_Catalogue.adapter.out;

import io.carlease.EV_Catalogue.adapter.out.persistence.VehicleMapper;
import io.carlease.EV_Catalogue.adapter.out.persistence.VehicleRepository;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VehiclePersistanceAdapter implements VehiclePersistancePort {

    private final VehicleRepository vehicleRepository;

    public VehiclePersistanceAdapter(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public void save(Vehicle vehicle) {
        vehicleRepository.save(VehicleMapper.mapToJpaEntity(vehicle));
    }

    @Override
    public boolean existsById(VehicleId vehicleId) {
        return vehicleRepository.existsById(vehicleId.getId());
    }

    @Override
    public Optional<Vehicle> findById(VehicleId vehicleId) {
        return vehicleRepository.findById(vehicleId.getId())
                .map(VehicleMapper::mapToDomain);
    }

    @Override
    public void deleteById(VehicleId vehicleId) {
        vehicleRepository.deleteById(vehicleId.getId());
    }
}
