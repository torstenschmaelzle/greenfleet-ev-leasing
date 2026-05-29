package io.carlease.sales.application.port.in;

import io.carlease.sales.domain.ContractNumber;
import io.carlease.shared.domain.BundleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public interface CreateFleetContractUseCase {

    /**
     * One entry per vehicle in the selected bundle.
     * monthlyInstallmentCents is the health-adjusted lease price from the EV catalogue.
     */
    record VehicleEntry(
            @NotBlank(message = "vin must not be blank")
            String vin,

            @Min(value = 0, message = "monthlyInstallmentCents must not be negative")
            long monthlyInstallmentCents,

            @NotBlank(message = "currency must not be blank")
            @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be a 3-letter ISO code (e.g. CHF, EUR)")
            String currency
    ) {}

    /**
     * Creates a FleetContract, notifies Risk Management, and returns the generated contract number.
     */
    ContractNumber create(
            String customerName,
            String customerCompany,
            String customerEmail,
            int leaseTermMonths,
            BundleType bundleType,
            List<VehicleEntry> vehicles
    );
}
