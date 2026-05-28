package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.*;
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

    private final VehiclePersistancePort persistancePort = mock(VehiclePersistancePort.class);
    private final RemoveVehicleService service = new RemoveVehicleService(persistancePort);

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
            given(persistancePort.findById(validVehicleId()))
                    .willReturn(Optional.of(existingVehicle()));

            service.remove(validVehicleId());

            then(persistancePort).should().deleteById(validVehicleId());
        }
    }

    @Nested
    class whenVehicleNotFound {

        @Test
        void throwsIllegalArgumentException() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.remove(validVehicleId()))
                    .isInstanceOf(VehicleNotFoundException.class);
        }

        @Test
        void doesNotDeleteWhenVehicleNotFound() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.remove(validVehicleId()))
                    .isInstanceOf(VehicleNotFoundException.class);

            then(persistancePort).should(never()).deleteById(any());
        }
    }

    @Nested
    class whenVehicleIsNotAvailable {

        @Test
        void throwsVehicleRemovalException() {
            given(persistancePort.findById(validVehicleId()))
                    .willReturn(Optional.of(reservedVehicle()));

            assertThatThrownBy(() -> service.remove(validVehicleId()))
                    .isInstanceOf(VehicleRemovalException.class);
        }

        @Test
        void doesNotDeleteWhenVehicleIsNotAvailable() {
            given(persistancePort.findById(validVehicleId()))
                    .willReturn(Optional.of(reservedVehicle()));

            assertThatThrownBy(() -> service.remove(validVehicleId()))
                    .isInstanceOf(VehicleRemovalException.class);

            then(persistancePort).should(never()).deleteById(any());
        }
    }
}
