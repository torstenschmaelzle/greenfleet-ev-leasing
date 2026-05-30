package io.carlease.sales.application.service;

import io.carlease.ev_catalogue.application.port.out.VehicleBroadcastPort;
import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.application.service.PlaceVehicleUnderContractService;
import io.carlease.ev_catalogue.domain.*;
import io.carlease.sales.adapter.out.catalogue.EVCataloguePlaceVehicleUnderContractAdapter;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.application.port.out.PlaceVehicleUnderContractPort;
import io.carlease.sales.domain.ContractFactory;
import io.carlease.sales.domain.ContractNumber;
import io.carlease.sales.domain.Customer;
import io.carlease.sales.domain.FleetContract;
import io.carlease.sales.domain.SignDate;
import io.carlease.shared.domain.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

/**
 * Caller:  SignFleetContractService                    (Sales bounded context)
 * Callee:  PlaceVehicleUnderContractService            (EV Catalogue bounded context)
 * Bridge:  EVCataloguePlaceVehicleUnderContractAdapter (Sales → EV Catalogue adapter)
 */
class SignFleetContractBoundedContextIntegrationTest {

    private static final String VIN = "WBA00000000000501";

    private Vehicle reservedVehicle(VehicleId id) {
        Vehicle v = VehicleFactory.createVehicle(
                id,
                VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                        EnergyConsumption.of(new BigDecimal("17"))),
                BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11")),
                Performance.of(286, new BigDecimal("6.8"), 180),
                Amount.ofCents(75000L, Currency.CHF),
                Percentage.of(new BigDecimal("95")),
                Range.ofKm(440)
        );
        v.lockVehicle(LockDuration.defaultDuration());
        return v;
    }

    private FleetContract unsignedContractWithVin(String vin) {
        var vehicleContract = ContractFactory.restoreFleetVehicleContract(
                ContractNumber.generate(),
                Customer.of("John Buyer", "ACME AG", "john@example.com"),
                io.carlease.sales.domain.Vehicle.of(vin),
                Amount.ofCents(300000L, Currency.CHF),
                Amount.ofCents(60000L, Currency.CHF),
                Optional.empty()
        );
        return ContractFactory.createFleetContract(
                ContractNumber.generate(),
                Customer.of("John Buyer", "ACME AG", "john@example.com"),
                LeaseTerm.of(36),
                BundleType.BALANCED,
                List.of(vehicleContract)
        );
    }


    // Test 1 – Caller isolation
    // Verifies that SignFleetContractService delegates to PlaceVehicleUnderContractPort
    // with the correct VIN for each vehicle in the fleet contract.

    @Nested
    class CallerInvokesCalleeWithCorrectData {

        private final FleetContractPersistencePort persistencePort = mock(FleetContractPersistencePort.class);
        private final PlaceVehicleUnderContractPort placePort = mock(PlaceVehicleUnderContractPort.class);
        private final SignFleetContractService caller = new SignFleetContractService(persistencePort, placePort);

        @Test
        void callerPassesVinToPlaceVehicleUnderContractPort() {
            FleetContract contract = unsignedContractWithVin(VIN);
            given(persistencePort.with(contract.number())).willReturn(contract);

            caller.sign(contract.number(), SignDate.of(2026, 5, 7));

            ArgumentCaptor<String> captor = forClass(String.class);
            then(placePort).should().placeUnderContract(captor.capture());
            assertThat(captor.getValue()).isEqualTo(VIN);
        }
    }


    // Test 2 – Callee isolation
    // Verifies that PlaceVehicleUnderContractService transitions a RESERVED vehicle
    // to UNDER_CONTRACT status and persists the result.

    @Nested
    class CalleeProcessesInputCorrectly {

        private final VehiclePersistencePort vehiclePersistencePort = mock(VehiclePersistencePort.class);
        private final VehicleBroadcastPort broadcastPort = mock(VehicleBroadcastPort.class);
        private final PlaceVehicleUnderContractService callee =
                new PlaceVehicleUnderContractService(vehiclePersistencePort, broadcastPort);

        @Test
        void calleeTransitionsVehicleToUnderContractStatus() {
            VehicleId vehicleId = VehicleId.of(VIN);
            given(vehiclePersistencePort.findById(vehicleId)).willReturn(Optional.of(reservedVehicle(vehicleId)));

            callee.assign(vehicleId);

            ArgumentCaptor<Vehicle> captor = forClass(Vehicle.class);
            then(vehiclePersistencePort).should().save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(VehicleStatus.UNDER_CONTRACT);
        }
    }


    // Test 3 – End-to-end integration
    // Wires real implementations of all three classes together.
    // Only infrastructure ports (persistence, broadcast) are mocked.
    // Asserts that signing a fleet contract causes the vehicle to be persisted as UNDER_CONTRACT.

    @Nested
    class EndToEndIntegration {

        private final FleetContractPersistencePort fleetPersistencePort = mock(FleetContractPersistencePort.class);
        private final VehiclePersistencePort vehiclePersistencePort = mock(VehiclePersistencePort.class);
        private final VehicleBroadcastPort broadcastPort = mock(VehicleBroadcastPort.class);

        private final PlaceVehicleUnderContractService placeService =
                new PlaceVehicleUnderContractService(vehiclePersistencePort, broadcastPort);
        private final EVCataloguePlaceVehicleUnderContractAdapter adapter =
                new EVCataloguePlaceVehicleUnderContractAdapter(placeService);
        private final SignFleetContractService caller =
                new SignFleetContractService(fleetPersistencePort, adapter);

        @Test
        void signingFleetContractCausesVehicleToBePersistedAsUnderContract() {
            FleetContract contract = unsignedContractWithVin(VIN);
            VehicleId vehicleId = VehicleId.of(VIN);
            given(fleetPersistencePort.with(contract.number())).willReturn(contract);
            given(vehiclePersistencePort.findById(vehicleId)).willReturn(Optional.of(reservedVehicle(vehicleId)));

            caller.sign(contract.number(), SignDate.of(2026, 5, 7));

            ArgumentCaptor<Vehicle> captor = forClass(Vehicle.class);
            then(vehiclePersistencePort).should().save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(VehicleStatus.UNDER_CONTRACT);
        }
    }
}
