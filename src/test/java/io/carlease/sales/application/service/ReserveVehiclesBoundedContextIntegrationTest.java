package io.carlease.sales.application.service;

import io.carlease.ev_catalogue.application.port.out.VehicleBroadcastPort;
import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.application.service.LockVehicleService;
import io.carlease.ev_catalogue.domain.*;
import io.carlease.sales.adapter.out.catalogue.EVCatalogueReserveVehicleAdapter;
import io.carlease.sales.application.port.out.ReserveVehiclePort;
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
import static org.mockito.Mockito.times;

/**
 * Task 7.2 – Bounded Context Integration Testing
 *
 * Caller:  ReserveVehiclesService       (Sales bounded context)
 * Callee:  LockVehicleService           (EV Catalogue bounded context)
 * Bridge:  EVCatalogueReserveVehicleAdapter  (Sales → EV Catalogue adapter)
 */
class ReserveVehiclesBoundedContextIntegrationTest {

    private static final String VIN = "WBA00000000000101";

    private Vehicle availableVehicle(VehicleId id) {
        return VehicleFactory.createVehicle(
                id,
                VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                        EnergyConsumption.of(new BigDecimal("17"))),
                BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11")),
                Performance.of(286, new BigDecimal("6.8"), 180),
                Amount.ofCents(75000L, Currency.CHF),
                Percentage.of(new BigDecimal("95")),
                Range.ofKm(440)
        );
    }

    // Test 1 – Caller isolation
    // Verifies that ReserveVehiclesService delegates to ReserveVehiclePort
    // with the correct VIN for each entry in the list.

    @Nested
    class CallerInvokesCalleeWithCorrectData {

        private final ReserveVehiclePort reserveVehiclePort = mock(ReserveVehiclePort.class);
        private final ReserveVehiclesService caller = new ReserveVehiclesService(reserveVehiclePort);

        @Test
        void callerPassesEachVinToReserveVehiclePort() {
            caller.reserve(List.of(VIN));

            ArgumentCaptor<String> captor = forClass(String.class);
            then(reserveVehiclePort).should(times(1)).reserve(captor.capture());
            assertThat(captor.getValue()).isEqualTo(VIN);
        }
    }

    // Test 2 – Callee isolation
    // Verifies that LockVehicleService correctly processes the VehicleId and
    // LockDuration it receives and persists the vehicle with RESERVED status.

    @Nested
    class CalleeProcessesInputCorrectly {

        private final VehiclePersistencePort persistencePort = mock(VehiclePersistencePort.class);
        private final VehicleBroadcastPort broadcastPort = mock(VehicleBroadcastPort.class);
        private final LockVehicleService callee = new LockVehicleService(persistencePort, broadcastPort);

        @Test
        void calleeLocksVehicleAndPersistsReservedStatus() {
            VehicleId vehicleId = VehicleId.of(VIN);
            given(persistencePort.findById(vehicleId)).willReturn(Optional.of(availableVehicle(vehicleId)));

            callee.lock(vehicleId, LockDuration.defaultDuration());

            ArgumentCaptor<Vehicle> captor = forClass(Vehicle.class);
            then(persistencePort).should().save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(VehicleStatus.RESERVED);
        }
    }


    // Test 3 – End-to-end integration
    // Wires real implementations of all three classes together.
    // Only the infrastructure ports (persistence, broadcast) are mocked.
    // Asserts that a call to ReserveVehiclesService ultimately causes
    // LockVehicleService to persist the vehicle as RESERVED.

    @Nested
    class EndToEndIntegration {

        private final VehiclePersistencePort persistencePort = mock(VehiclePersistencePort.class);
        private final VehicleBroadcastPort broadcastPort = mock(VehicleBroadcastPort.class);

        private final LockVehicleService lockVehicleService =
                new LockVehicleService(persistencePort, broadcastPort);
        private final EVCatalogueReserveVehicleAdapter adapter =
                new EVCatalogueReserveVehicleAdapter(lockVehicleService);
        private final ReserveVehiclesService caller =
                new ReserveVehiclesService(adapter);

        @Test
        void reservingVinCausesVehicleToBePersistedAsReserved() {
            VehicleId vehicleId = VehicleId.of(VIN);
            given(persistencePort.findById(vehicleId)).willReturn(Optional.of(availableVehicle(vehicleId)));

            caller.reserve(List.of(VIN));

            ArgumentCaptor<Vehicle> captor = forClass(Vehicle.class);
            then(persistencePort).should().save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(VehicleStatus.RESERVED);
            assertThat(captor.getValue().getLock()).isPresent();
        }
    }
}
