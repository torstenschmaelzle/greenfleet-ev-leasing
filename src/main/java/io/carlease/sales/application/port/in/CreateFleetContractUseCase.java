package io.carlease.sales.application.port.in;

import io.carlease.sales.domain.FleetContractNumber;

import java.util.List;

public interface CreateFleetContractUseCase {

    /**
     * One entry per vehicle in the selected bundle.
     * basePriceCents is the health-adjusted lease price from the EV catalogue —
     * Sales uses it to recalculate the installment via its own domain logic.
     */
    record VehicleEntry(String vin, long monthlyInstallmentCents, String currency) {}

    /**
     * Creates a FleetContract, auto-approves it (notifies Risk Management),
     * locks all vehicles in EV Catalogue, and returns the generated contract number.
     *
     * TODO: remove auto-approve once the Risk Management feedback loop is implemented
     * and RM vote can actually block signing.
     */
    FleetContractNumber create(
            String customerName,
            String customerCompany,
            String customerEmail,
            int leaseTermMonths,
            String bundleType,
            List<VehicleEntry> vehicles
    );
}
