package io.carlease.sales.domain;

import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.LeaseTerm;
import org.jmolecules.ddd.annotation.Factory;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Factory
public class ContractFactory {

    public static VehicleContract createFleetVehicleContract(
            ContractNumber number, Customer lessee, Vehicle vehicle, Amount installment) {
        requireNonNull(number);
        requireNonNull(lessee);
        requireNonNull(vehicle);
        requireNonNull(installment);
        return new VehicleContract(number, lessee, vehicle, installment, installment, Optional.empty());
    }

    public static VehicleContract restoreFleetVehicleContract(
            ContractNumber number, Customer lessee, Vehicle vehicle, Amount price,
            Amount installment, Optional<SignDate> signatureDate) {
        requireNonNull(number);
        requireNonNull(lessee);
        requireNonNull(vehicle);
        requireNonNull(price);
        requireNonNull(installment);
        return new VehicleContract(number, lessee, vehicle, price, installment, signatureDate);
    }

    public static FleetContract createFleetContract(
            ContractNumber number, Customer customer, LeaseTerm leaseTerm,
            String bundleType, List<VehicleContract> vehicleContracts) {
        requireNonNull(number);
        requireNonNull(customer);
        requireNonNull(leaseTerm);
        requireNonNull(bundleType);
        requireNonNull(vehicleContracts);
        return new FleetContract(number, customer, leaseTerm, bundleType, vehicleContracts, Optional.empty());
    }

    public static FleetContract restoreFleetContract(
            ContractNumber number, Customer customer, LeaseTerm leaseTerm,
            String bundleType, List<VehicleContract> vehicleContracts,
            Optional<SignDate> signatureDate) {
        requireNonNull(number);
        requireNonNull(customer);
        requireNonNull(leaseTerm);
        requireNonNull(bundleType);
        requireNonNull(vehicleContracts);
        return new FleetContract(number, customer, leaseTerm, bundleType, vehicleContracts, signatureDate);
    }
}
