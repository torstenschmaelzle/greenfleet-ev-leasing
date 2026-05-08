package io.carlease.sales.application.port.in;

import io.carlease.sales.domain.FleetContract;

import java.util.List;

public interface ListMyContractsUseCase {
    List<FleetContract> listByEmail(String email);
}
