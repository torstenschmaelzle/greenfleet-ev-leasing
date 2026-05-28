package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.out.VehicleBroadcastPort;
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

class LockVehicleServiceTest {

    private final VehiclePersistancePort persistancePort = mock(VehiclePersistancePort.class);
    private final VehicleBroadcastPort broadcaster = mock(VehicleBroadcastPort.class);
    private final LockVehicleService service = new LockVehicleService(persistancePort, broadcaster);

    private VehicleId validVehicleId() {
        return VehicleId.of("WBA12345678654234");
    }

    private LockDuration validDuration() {
        return LockDuration.ofMinutes(120);
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
    class whenVehicleIsAvailable {

        @Test
        void savesVehicleWithStatusReserved() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.of(availableVehicle()));

            service.lock(validVehicleId(), validDuration());

            ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
            then(persistancePort).should().save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(VehicleStatus.RESERVED);
        }

        @Test
        void broadcastsReservedStatus() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.of(availableVehicle()));

            service.lock(validVehicleId(), validDuration());

            then(broadcaster).should().broadcast(validVehicleId().getVin(), VehicleStatus.RESERVED);
        }
    }

    @Nested
    class whenVehicleNotFound {

        @Test
        void throwsIllegalArgumentException() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.lock(validVehicleId(), validDuration()))
                    .isInstanceOf(VehicleNotFoundException.class);
        }

        @Test
        void doesNotSaveWhenVehicleNotFound() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.lock(validVehicleId(), validDuration()))
                    .isInstanceOf(VehicleNotFoundException.class);

            then(persistancePort).should(never()).save(any());
        }
    }

    @Nested
    class whenVehicleIsNotAvailable {

        @Test
        void throwsVehicleNotAvailableExceptionForReservedVehicle() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.of(reservedVehicle()));

            assertThatThrownBy(() -> service.lock(validVehicleId(), validDuration()))
                    .isInstanceOf(VehicleNotAvailableException.class);
        }

        @Test
        void doesNotSaveWhenVehicleIsNotAvailable() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.of(reservedVehicle()));

            assertThatThrownBy(() -> service.lock(validVehicleId(), validDuration()))
                    .isInstanceOf(VehicleNotAvailableException.class);

            then(persistancePort).should(never()).save(any());
        }
    }
}
