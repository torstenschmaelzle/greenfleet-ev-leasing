package io.carlease.sales.application.service;

import io.carlease.riskmanagement.application.port.in.AssessApprovedContractPort;
import io.carlease.sales.application.port.in.CreateFleetContractUseCase;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CreateFleetContractService implements CreateFleetContractUseCase {

    private final FleetContractPersistencePort fleetContractPersistencePort;
    private final AssessApprovedContractPort assessApprovedContractPort;

    public CreateFleetContractService(FleetContractPersistencePort fleetContractPersistencePort,
                                      AssessApprovedContractPort assessApprovedContractPort) {
        this.fleetContractPersistencePort = fleetContractPersistencePort;
        this.assessApprovedContractPort = assessApprovedContractPort;
    }

    @Override
    public FleetContractNumber create(String customerName, String customerCompany, String customerEmail,
                                      int leaseTermMonths, String bundleType, List<VehicleEntry> vehicles) {

        FleetContractNumber number = FleetContractNumber.of("FC-" + UUID.randomUUID());
        LeaseTerm leaseTerm = LeaseTerm.ofMonths(leaseTermMonths);
        Customer customer = Customer.of(customerName, customerCompany, customerEmail);

        List<VehicleContract> vehicleContracts = vehicles.stream()
                .map(entry -> buildVehicleContract(entry, number))
                .toList();

        FleetContract fleetContract = new FleetContract(number, customer, leaseTerm, bundleType, vehicleContracts);

        // Auto-approve immediately — the fleet agent already validated this recommendation.
        // TODO: remove auto-approve once the Risk Management feedback loop is implemented
        SignDate today = SignDate.of(LocalDate.now());
        fleetContract.approve(today);

        fleetContractPersistencePort.save(fleetContract);

        // Notify Risk Management so a worker can perform a credit check in parallel
        assessApprovedContractPort.with(
                number.number(),
                today.year(), today.month(), today.day()
        );

        // Vehicles are already RESERVED — set to UNDER_CONTRACT happens on signing
        return number;
    }

    private VehicleContract buildVehicleContract(VehicleEntry entry, FleetContractNumber fleetNumber) {
        String contractNumber = fleetNumber.number() + "-" + entry.vin();
        Currency currency = Currency.valueOf(entry.currency());
        Amount installment = Amount.ofCents(entry.monthlyInstallmentCents(), currency);

        VehicleContract vc = new VehicleContract(
                ContractNumber.of(contractNumber),
                Customer.of("Fleet"),
                Car.of(entry.vin()),
                installment
        );
        vc.acceptInstallment(installment);
        return vc;
    }
}
