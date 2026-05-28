package io.carlease.sales.application.service;

import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.domain.*;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.LeaseTerm;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class OnVehicleLockExpiredServiceTest {

    private final FleetContractPersistencePort persistencePort = mock(FleetContractPersistencePort.class);
    private final OnVehicleLockExpiredService service = new OnVehicleLockExpiredService(persistencePort);

    private FleetContract unsignedContract(String... vins) {
        var vehicleContracts = java.util.Arrays.stream(vins).map(vin ->
            ContractFactory.restoreFleetVehicleContract(
                ContractNumber.generate(),
                Customer.of("Test", "Test AG", "test@test.com"),
                Vehicle.of(vin),
                Amount.ofCents(75000L, Currency.CHF),
                Amount.ofCents(75000L, Currency.CHF),
                Optional.empty()
            )
        ).toList();
        return ContractFactory.createFleetContract(
            ContractNumber.generate(),
            Customer.of("Test", "Test AG", "test@test.com"),
            LeaseTerm.of(36),
            "BALANCED",
            vehicleContracts
        );
    }

    private FleetContract signedContract(String... vins) {
        FleetContract contract = unsignedContract(vins);
        contract.sign(SignDate.of(2026, 1, 1));
        return contract;
    }

    @Nested
    class whenUnsignedContractContainsExpiredVin {

        @Test
        void deletesContract() {
            FleetContract contract = unsignedContract("WBA00000000000101");
            given(persistencePort.listAll()).willReturn(List.of(contract));

            service.handle(List.of("WBA00000000000101"));

            then(persistencePort).should().delete(contract.number());
        }

        @Test
        void deletesAllMatchingUnsignedContracts() {
            FleetContract c1 = unsignedContract("WBA00000000000101");
            FleetContract c2 = unsignedContract("WBA00000000000102");
            given(persistencePort.listAll()).willReturn(List.of(c1, c2));

            service.handle(List.of("WBA00000000000101", "WBA00000000000102"));

            then(persistencePort).should().delete(c1.number());
            then(persistencePort).should().delete(c2.number());
        }

        @Test
        void deletesContractWhenOnlyOneOfItsVehiclesExpired() {
            // fleet contract with 2 vehicles — only one lock expired
            // the whole contract is still deleted because signing requires ALL vehicles to be RESERVED
            FleetContract contract = unsignedContract("WBA00000000000101", "WBA00000000000999");
            given(persistencePort.listAll()).willReturn(List.of(contract));

            service.handle(List.of("WBA00000000000101"));

            then(persistencePort).should().delete(contract.number());
        }
    }

    @Nested
    class whenSignedContractContainsExpiredVin {

        @Test
        void doesNotDeleteSignedContract() {
            FleetContract signed = signedContract("WBA00000000000101");
            given(persistencePort.listAll()).willReturn(List.of(signed));

            service.handle(List.of("WBA00000000000101"));

            then(persistencePort).should(never()).delete(any());
        }
    }

    @Nested
    class whenNoContractContainsExpiredVin {

        @Test
        void doesNotDeleteAnyContract() {
            FleetContract contract = unsignedContract("WBA00000000000999");
            given(persistencePort.listAll()).willReturn(List.of(contract));

            service.handle(List.of("WBA00000000000101"));

            then(persistencePort).should(never()).delete(any());
        }
    }

    @Nested
    class whenNoContractsExist {

        @Test
        void doesNotDelete() {
            given(persistencePort.listAll()).willReturn(List.of());

            service.handle(List.of("WBA00000000000101"));

            then(persistencePort).should(never()).delete(any());
        }
    }
}
