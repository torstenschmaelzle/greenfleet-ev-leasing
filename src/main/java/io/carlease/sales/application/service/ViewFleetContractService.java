package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.ViewFleetContractUseCase;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.domain.FleetContract;
import io.carlease.sales.domain.FleetContractNumber;
import org.springframework.stereotype.Service;

@Service
public class ViewFleetContractService implements ViewFleetContractUseCase {

    private final FleetContractPersistencePort fleetContractPersistencePort;

    public ViewFleetContractService(FleetContractPersistencePort fleetContractPersistencePort) {
        this.fleetContractPersistencePort = fleetContractPersistencePort;
    }

    @Override
    public FleetContract with(FleetContractNumber number) {
        return fleetContractPersistencePort.with(number);
    }
}
