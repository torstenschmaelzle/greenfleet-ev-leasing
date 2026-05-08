package io.carlease.sales.application.port.out;

import io.carlease.sales.domain.ContractNumber;
import io.carlease.sales.domain.FleetContract;

public interface FleetContractPersistencePort {
    void save(FleetContract fleetContract);
    FleetContract with(ContractNumber number);
}
