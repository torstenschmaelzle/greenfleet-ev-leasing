package io.carlease.sales.application.port.out;

import io.carlease.sales.domain.FleetContract;
import io.carlease.sales.domain.FleetContractNumber;

public interface FleetContractPersistencePort {
    void save(FleetContract fleetContract);
    FleetContract with(FleetContractNumber number);
    boolean exists(FleetContractNumber number);
}
