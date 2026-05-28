package io.carlease.ev_catalogue.adapter.out;

import io.carlease.ev_catalogue.adapter.out.persistence.VehicleMapper;
import io.carlease.ev_catalogue.adapter.out.persistence.VehicleRepository;
import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.Vehicle;
import io.carlease.ev_catalogue.domain.VehicleId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class VehiclePersistenceAdapter implements VehiclePersistencePort {

    private final VehicleRepository vehicleRepository;

    public VehiclePersistenceAdapter(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public void save(Vehicle vehicle) {
        vehicleRepository.save(VehicleMapper.mapToJpaEntity(vehicle));
    }

    @Override
    public boolean existsById(VehicleId vehicleId) {
        return vehicleRepository.existsById(vehicleId.getVin());
    }

    @Override
    public Optional<Vehicle> findById(VehicleId vehicleId) {
        return vehicleRepository.findById(vehicleId.getVin())
                .map(VehicleMapper::mapToDomain);
    }

    @Override
    public void deleteById(VehicleId vehicleId) {
        vehicleRepository.deleteById(vehicleId.getVin());
    }

    @Override
    public List<Vehicle> findAllReserved() {
        return vehicleRepository.findByStatus("RESERVED").stream()
                .map(VehicleMapper::mapToDomain)
                .toList();
    }
}
