package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.*;
import io.carlease.shared.domain.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class AssignVehicleToContractServiceTest {

    private final VehiclePersistancePort persistancePort = mock(VehiclePersistancePort.class);
    private final VehicleStatusBroadcaster broadcaster = mock(VehicleStatusBroadcaster.class);
    private final AssignVehicleToContractService service =
            new AssignVehicleToContractService(persistancePort, broadcaster);

    private VehicleId validVehicleId() {
        return VehicleId.of("WBA12345678654234");
    }

    private Vehicle availableVehicle() {
        return VehicleFactory.createVehicle(
                validVehicleId(),
                VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                        EnergyConsumption.of(new BigDecimal("17"))),
                BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11")),
                Performance.of(286, new BigDecimal("6.8"), 180),
                Amount.ofCents(75000L, Currency.CHF),
                Percentage.of(new BigDecimal("95")),
                Range.ofKm(440)
        );
    }

    private Vehicle reservedVehicle() {
        Vehicle vehicle = availableVehicle();
        vehicle.lockVehicle(LockDuration.defaultDuration());
        return vehicle;
    }

    @Nested
    class whenVehicleIsReserved {

        @Test
        void savesVehicleWithStatusUnderContract() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.of(reservedVehicle()));

            service.assign(validVehicleId());

            ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
            then(persistancePort).should().save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(VehicleStatus.UNDER_CONTRACT);
        }

        @Test
        void broadcastsUnderContractStatus() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.of(reservedVehicle()));

            service.assign(validVehicleId());

            then(broadcaster).should().broadcast(validVehicleId().getVin(), "UNDER_CONTRACT");
        }
    }

    @Nested
    class whenVehicleNotFound {

        @Test
        void throwsIllegalArgumentException() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.assign(validVehicleId()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void doesNotSaveWhenVehicleNotFound() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.assign(validVehicleId()))
                    .isInstanceOf(IllegalArgumentException.class);

            then(persistancePort).should(never()).save(any());
        }
    }

    @Nested
    class whenVehicleIsNotReserved {

        @Test
        void throwsVehicleContractViolationExceptionForAvailableVehicle() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.of(availableVehicle()));

            assertThatThrownBy(() -> service.assign(validVehicleId()))
                    .isInstanceOf(VehicleContractViolationException.class);
        }

        @Test
        void doesNotSaveWhenVehicleIsNotReserved() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.of(availableVehicle()));

            assertThatThrownBy(() -> service.assign(validVehicleId()))
                    .isInstanceOf(VehicleContractViolationException.class);

            then(persistancePort).should(never()).save(any());
        }
    }
}
