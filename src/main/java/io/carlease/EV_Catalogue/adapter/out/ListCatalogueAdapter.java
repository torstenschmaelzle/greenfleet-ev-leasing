package io.carlease.ev_catalogue.adapter.out;

import io.carlease.ev_catalogue.adapter.out.persistence.VehicleMapper;
import io.carlease.ev_catalogue.adapter.out.persistence.VehicleRepository;
import io.carlease.ev_catalogue.application.port.out.ListCataloguePort;
import io.carlease.ev_catalogue.domain.Vehicle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class ListCatalogueAdapter implements ListCataloguePort {

    private final VehicleRepository vehicleRepository;

    public ListCatalogueAdapter(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<Vehicle> listAll() {
        return StreamSupport.stream(vehicleRepository.findAll().spliterator(), false)
                .map(VehicleMapper::mapToDomain)
                .collect(Collectors.toList());
    }
}
