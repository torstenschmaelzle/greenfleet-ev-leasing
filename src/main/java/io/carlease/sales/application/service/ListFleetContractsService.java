package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.ListFleetContractsUseCase;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.domain.FleetContract;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListFleetContractsService implements ListFleetContractsUseCase {

    private final FleetContractPersistencePort fleetContractPersistencePort;

    public ListFleetContractsService(FleetContractPersistencePort fleetContractPersistencePort) {
        this.fleetContractPersistencePort = fleetContractPersistencePort;
    }

    @Override
    public List<FleetContract> listAll() {
        return fleetContractPersistencePort.listAll();
    }
}
