package io.carlease.sales.application.port.in;

import io.carlease.sales.domain.ContractNumber;
import io.carlease.sales.domain.FleetContract;

public interface ViewFleetContractUseCase {
    FleetContract with(ContractNumber number);
}
