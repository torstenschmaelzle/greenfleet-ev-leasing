package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.CreateFleetContractUseCase;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.application.port.out.LockVehiclePort;
import io.carlease.sales.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateFleetContractService implements CreateFleetContractUseCase {

    private static final double DEFAULT_INTEREST_RATE_PER_YEAR = 2.9;

    private final FleetContractPersistencePort fleetContractPersistencePort;
    private final LockVehiclePort lockVehiclePort;

    public CreateFleetContractService(FleetContractPersistencePort fleetContractPersistencePort,
                                      LockVehiclePort lockVehiclePort) {
        this.fleetContractPersistencePort = fleetContractPersistencePort;
        this.lockVehiclePort = lockVehiclePort;
    }

    @Override
    public FleetContractNumber create(String fleetContractNumber, String customer,
                                      int leaseTermMonths, String bundleType,
                                      List<VehicleEntry> vehicles) {
        FleetContractNumber number = FleetContractNumber.of(fleetContractNumber);
        LeaseTerm leaseTerm = LeaseTerm.ofMonths(leaseTermMonths);
        Interest interest = Interest.of(DEFAULT_INTEREST_RATE_PER_YEAR);

        List<VehicleContract> vehicleContracts = vehicles.stream()
                .map(entry -> buildVehicleContract(entry, leaseTerm, interest))
                .toList();

        FleetContract fleetContract = new FleetContract(
                number,
                Customer.of(customer),
                leaseTerm,
                bundleType,
                vehicleContracts
        );

        fleetContractPersistencePort.save(fleetContract);

        vehicles.forEach(entry -> lockVehiclePort.lock(entry.vin()));

        return number;
    }

    private VehicleContract buildVehicleContract(VehicleEntry entry, LeaseTerm leaseTerm, Interest interest) {
        Currency currency = Currency.valueOf(entry.currency());
        VehicleContract vc = new VehicleContract(
                ContractNumber.of(entry.contractNumber()),
                Customer.of("Fleet"),
                Car.of(entry.vin()),
                Amount.ofCents(entry.priceInCents(), currency)
        );
        vc.calculateInstallmentFor(leaseTerm, interest);
        return vc;
    }
}
