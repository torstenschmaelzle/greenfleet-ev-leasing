package io.carlease.sales.application.port.in;

import io.carlease.sales.domain.FleetContract;
import io.carlease.sales.domain.FleetContractNumber;

public interface ViewFleetContractUseCase {
    FleetContract with(FleetContractNumber number);
}
