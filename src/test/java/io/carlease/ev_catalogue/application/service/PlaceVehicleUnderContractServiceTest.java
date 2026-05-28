package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.out.VehicleBroadcastPort;
import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.*;
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

class PlaceVehicleUnderContractServiceTest {

    private final VehiclePersistencePort persistencePort = mock(VehiclePersistencePort.class);
    private final VehicleBroadcastPort broadcaster = mock(VehicleBroadcastPort.class);
    private final PlaceVehicleUnderContractService service =
            new PlaceVehicleUnderContractService(persistencePort, broadcaster);

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
            given(persistencePort.findById(validVehicleId())).willReturn(Optional.of(reservedVehicle()));

            service.assign(validVehicleId());

            ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
            then(persistencePort).should().save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(VehicleStatus.UNDER_CONTRACT);
        }

        @Test
        void broadcastsUnderContractStatus() {
            given(persistencePort.findById(validVehicleId())).willReturn(Optional.of(reservedVehicle()));

            service.assign(validVehicleId());

            then(broadcaster).should().broadcast(validVehicleId().getVin(), VehicleStatus.UNDER_CONTRACT);
        }
    }

    @Nested
    class whenVehicleNotFound {

        @Test
        void throwsIllegalArgumentException() {
            given(persistencePort.findById(validVehicleId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.assign(validVehicleId()))
                    .isInstanceOf(VehicleNotFoundException.class);
        }

        @Test
        void doesNotSaveWhenVehicleNotFound() {
            given(persistencePort.findById(validVehicleId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.assign(validVehicleId()))
                    .isInstanceOf(VehicleNotFoundException.class);

            then(persistencePort).should(never()).save(any());
        }
    }

    @Nested
    class whenVehicleIsNotReserved {

        @Test
        void throwsVehicleUnderContractViolationExceptionForAvailableVehicle() {
            given(persistencePort.findById(validVehicleId())).willReturn(Optional.of(availableVehicle()));

            assertThatThrownBy(() -> service.assign(validVehicleId()))
                    .isInstanceOf(VehicleUnderContractViolationException.class);
        }

        @Test
        void doesNotSaveWhenVehicleIsNotReserved() {
            given(persistencePort.findById(validVehicleId())).willReturn(Optional.of(availableVehicle()));

            assertThatThrownBy(() -> service.assign(validVehicleId()))
                    .isInstanceOf(VehicleUnderContractViolationException.class);

            then(persistencePort).should(never()).save(any());
        }
    }
}
