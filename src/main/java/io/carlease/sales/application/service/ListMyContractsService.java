package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.ListMyContractsUseCase;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.domain.FleetContract;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListMyContractsService implements ListMyContractsUseCase {

    private final FleetContractPersistencePort fleetContractPersistencePort;

    public ListMyContractsService(FleetContractPersistencePort fleetContractPersistencePort) {
        this.fleetContractPersistencePort = fleetContractPersistencePort;
    }

    @Override
    public List<FleetContract> listByEmail(String email) {
        return fleetContractPersistencePort.findByCustomerEmail(email);
    }
}
