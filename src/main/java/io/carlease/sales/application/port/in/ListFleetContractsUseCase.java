package io.carlease.sales.application.port.in;

import io.carlease.sales.domain.FleetContract;

import java.util.List;

public interface ListFleetContractsUseCase {
    List<FleetContract> listAll();
}
