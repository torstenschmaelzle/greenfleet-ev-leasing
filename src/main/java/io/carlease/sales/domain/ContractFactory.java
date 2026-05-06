package io.carlease.sales.domain;

import org.jmolecules.ddd.annotation.Factory;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Factory
public class ContractFactory {

    public static VehicleContract restoreVehicleContract(
            ContractNumber number, Customer lessee, Car car, Amount price,
            Optional<LeaseTerm> leaseTerm, Optional<Interest> interest,
            Optional<SignDate> approvalDate, Optional<SignDate> signatureDate,
            Amount downPayment, Amount residualValue) {
        requireNonNull(number);
        requireNonNull(lessee);
        requireNonNull(car);
        requireNonNull(price);

        var contract = new VehicleContract(number, lessee, car, price);
        if (leaseTerm.isPresent() && interest.isPresent()) {
            contract.calculateInstallmentFor(leaseTerm.get(), interest.get(), downPayment, residualValue);
        }
        approvalDate.ifPresent(contract::approve);
        signatureDate.ifPresent(contract::sign);

        return contract;
    }

    public static VehicleContract restoreFleetVehicleContract(
            ContractNumber number, Customer lessee, Car car, Amount price,
            Amount monthlyInstallment,
            Optional<SignDate> approvalDate, Optional<SignDate> signatureDate) {
        requireNonNull(number);
        requireNonNull(lessee);
        requireNonNull(car);
        requireNonNull(price);
        requireNonNull(monthlyInstallment);

        var contract = new VehicleContract(number, lessee, car, price);
        contract.acceptInstallment(monthlyInstallment);
        approvalDate.ifPresent(contract::approve);
        signatureDate.ifPresent(contract::sign);
        return contract;
    }

    public static FleetContract restoreFleetContract(
            FleetContractNumber number, Customer customer, LeaseTerm leaseTerm,
            String bundleType, List<VehicleContract> vehicleContracts,
            Optional<SignDate> approvalDate, Optional<SignDate> signatureDate) {
        requireNonNull(number);
        requireNonNull(customer);
        requireNonNull(leaseTerm);
        requireNonNull(bundleType);
        requireNonNull(vehicleContracts);

        var fleetContract = new FleetContract(number, customer, leaseTerm, bundleType, vehicleContracts);
        approvalDate.ifPresent(fleetContract::approve);
        signatureDate.ifPresent(fleetContract::sign);

        return fleetContract;
    }
}
