package io.carlease.sales.application.port.in;

import io.carlease.sales.domain.FleetContractNumber;

import java.util.List;

public interface CreateFleetContractUseCase {

    record VehicleEntry(String contractNumber, String vin, long priceInCents, String currency) {}

    FleetContractNumber create(
            String fleetContractNumber,
            String customer,
            int leaseTermMonths,
            String bundleType,
            List<VehicleEntry> vehicles
    );
}
