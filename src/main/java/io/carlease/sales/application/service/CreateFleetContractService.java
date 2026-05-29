package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.CreateFleetContractUseCase;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.domain.*;
import io.carlease.shared.domain.BundleType;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.LeaseTerm;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateFleetContractService implements CreateFleetContractUseCase {

    private final FleetContractPersistencePort fleetContractPersistencePort;

    public CreateFleetContractService(FleetContractPersistencePort fleetContractPersistencePort) {
        this.fleetContractPersistencePort = fleetContractPersistencePort;
    }

    @Override
    public ContractNumber create(String customerName, String customerCompany, String customerEmail,
                                 int leaseTermMonths, BundleType bundleType, List<VehicleEntry> vehicles) {

        ContractNumber number = ContractNumber.generate();
        LeaseTerm leaseTerm = LeaseTerm.ofMonths(leaseTermMonths);
        Customer customer = Customer.of(customerName, customerCompany, customerEmail);

        List<VehicleContract> vehicleContracts = vehicles.stream()
                .map(entry -> buildVehicleContract(entry, customer))
                .toList();

        FleetContract fleetContract = ContractFactory.createFleetContract(number, customer, leaseTerm, bundleType, vehicleContracts);
        fleetContractPersistencePort.save(fleetContract);

        return number;
    }

    private VehicleContract buildVehicleContract(VehicleEntry entry, Customer customer) {
        Currency currency = Currency.from(entry.currency());
        Amount installment = Amount.ofCents(entry.monthlyInstallmentCents(), currency);
        return ContractFactory.createFleetVehicleContract(
                ContractNumber.generate(),
                customer,
                Vehicle.of(entry.vin()),
                installment
        );
    }
}
