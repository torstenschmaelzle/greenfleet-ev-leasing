package io.carlease.sales.application.port.in;

import io.carlease.sales.domain.FleetContractNumber;
import io.carlease.sales.domain.SignDate;

public interface SignFleetContractUseCase {
    void sign(FleetContractNumber number, SignDate date);
}
