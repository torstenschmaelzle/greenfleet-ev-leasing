package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.*;
import io.carlease.shared.domain.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class RemoveVehicleServiceTest {

    private final VehiclePersistencePort persistencePort = mock(VehiclePersistencePort.class);
    private final RemoveVehicleService service = new RemoveVehicleService(persistencePort);

    private VehicleId validVehicleId() {
        return VehicleId.of("WBA12345678654234");
    }

    private Vehicle existingVehicle() {
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
        Vehicle vehicle = existingVehicle();
        vehicle.lockVehicle(LockDuration.defaultDuration());
        return vehicle;
    }

    @Nested
    class whenVehicleExistsAndIsAvailable {

        @Test
        void deletesVehicle() {
            given(persistencePort.findById(validVehicleId()))
                    .willReturn(Optional.of(existingVehicle()));

            service.remove(validVehicleId());

            then(persistencePort).should().deleteById(validVehicleId());
        }
    }

    @Nested
    class whenVehicleNotFound {

        @Test
        void throwsIllegalArgumentException() {
            given(persistencePort.findById(validVehicleId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.remove(validVehicleId()))
                    .isInstanceOf(VehicleNotFoundException.class);
        }

        @Test
        void doesNotDeleteWhenVehicleNotFound() {
            given(persistencePort.findById(validVehicleId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.remove(validVehicleId()))
                    .isInstanceOf(VehicleNotFoundException.class);

            then(persistencePort).should(never()).deleteById(any());
        }
    }

    @Nested
    class whenVehicleIsNotAvailable {

        @Test
        void throwsVehicleRemovalException() {
            given(persistencePort.findById(validVehicleId()))
                    .willReturn(Optional.of(reservedVehicle()));

            assertThatThrownBy(() -> service.remove(validVehicleId()))
                    .isInstanceOf(VehicleRemovalException.class);
        }

        @Test
        void doesNotDeleteWhenVehicleIsNotAvailable() {
            given(persistencePort.findById(validVehicleId()))
                    .willReturn(Optional.of(reservedVehicle()));

            assertThatThrownBy(() -> service.remove(validVehicleId()))
                    .isInstanceOf(VehicleRemovalException.class);

            then(persistencePort).should(never()).deleteById(any());
        }
    }
}
