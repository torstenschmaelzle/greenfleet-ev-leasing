package io.carlease.sales.application.service;

import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.application.port.out.PlaceVehicleUnderContractPort;
import io.carlease.sales.domain.*;
import io.carlease.shared.domain.BundleType;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.LeaseTerm;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class SignFleetContractServiceTest {

    private final FleetContractPersistencePort persistencePort = mock(FleetContractPersistencePort.class);
    private final PlaceVehicleUnderContractPort placePort = mock(PlaceVehicleUnderContractPort.class);
    private final SignFleetContractService service = new SignFleetContractService(persistencePort, placePort);

    private FleetContract unsignedContractWithVins(String... vins) {
        var vehicleContracts = java.util.Arrays.stream(vins).map(vin ->
            ContractFactory.restoreFleetVehicleContract(
                ContractNumber.generate(),
                Customer.of("John Buyer", "ACME AG", "john@example.com"),
                Vehicle.of(vin),
                Amount.ofCents(300000L, Currency.CHF),
                Amount.ofCents(60000L, Currency.CHF),
                Optional.empty()
            )
        ).toList();
        return ContractFactory.createFleetContract(
            ContractNumber.generate(),
            Customer.of("John Buyer", "ACME AG", "john@example.com"),
            LeaseTerm.of(36),
            BundleType.BALANCED,
            vehicleContracts
        );
    }

    @Nested
    class whenContractExists {

        @Test
        void savedContractIsMarkedAsSigned() {
            var contract = unsignedContractWithVins("WBA00000000000101");
            given(persistencePort.with(contract.number())).willReturn(contract);

            service.sign(contract.number(), SignDate.of(2026, 5, 7));

            var captor = forClass(FleetContract.class);
            then(persistencePort).should().save(captor.capture());
            assertThat(captor.getValue().isSigned()).isTrue();
        }

        @Test
        void placeUnderContractIsCalledForEachVehicle() {
            var contract = unsignedContractWithVins("WBA00000000000101", "WBA00000000000102");
            given(persistencePort.with(contract.number())).willReturn(contract);

            service.sign(contract.number(), SignDate.of(2026, 5, 7));

            then(placePort).should().placeUnderContract("WBA00000000000101");
            then(placePort).should().placeUnderContract("WBA00000000000102");
        }
    }

    @Nested
    class whenContractDoesNotExist {

        @Test
        void propagatesContractNotFoundException() {
            var number = ContractNumber.generate();
            given(persistencePort.with(number)).willThrow(new ContractNotFoundException(number.number()));

            assertThatThrownBy(() -> service.sign(number, SignDate.of(2026, 5, 7)))
                .isInstanceOf(ContractNotFoundException.class);

            then(persistencePort).should(never()).save(any());
            then(placePort).should(never()).placeUnderContract(any());
        }
    }

    @Nested
    class whenVehicleReservationExpired {

        @Test
        void propagatesReservationExpiredException() {
            var contract = unsignedContractWithVins("WBA00000000000101");
            given(persistencePort.with(contract.number())).willReturn(contract);
            willThrow(new ReservationExpiredException("WBA00000000000101"))
                    .given(placePort).placeUnderContract("WBA00000000000101");

            assertThatThrownBy(() -> service.sign(contract.number(), SignDate.of(2026, 5, 7)))
                    .isInstanceOf(ReservationExpiredException.class);

            then(persistencePort).should().save(any());
        }
    }
}
