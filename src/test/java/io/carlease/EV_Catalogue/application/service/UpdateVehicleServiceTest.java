package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.*;
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

class UpdateVehicleServiceTest {

    private final VehiclePersistancePort persistancePort = mock(VehiclePersistancePort.class);
    private final UpdateVehicleService service = new UpdateVehicleService(persistancePort);

    private VehicleId validVehicleId() {
        return VehicleId.of("WBA12345678654234");
    }

    private VehicleSpecification validSpecification() {
        return VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                EnergyConsumption.of(new BigDecimal("17")));
    }

    private BatterySpecification validBatterySpecification() {
        return BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11"));
    }

    private Amount validMonthlyLeasePrice() {
        return Amount.ofCents(75000L, Currency.CHF);
    }

    private Percentage validNewStateOfHealth() {
        return Percentage.of(new BigDecimal("90"));
    }

    private Range validNewMeasuredRange() {
        return Range.ofKm(420);
    }

    private Vehicle existingVehicle() {
        return Vehicle.create(
                validVehicleId(),
                VehicleSpecification.of("Audi", "e-tron", Range.ofKm(300), VehicleType.SUV,
                        EnergyConsumption.of(new BigDecimal("22"))),
                BatterySpecification.of(new BigDecimal("71"), new BigDecimal("396"), new BigDecimal("7")),
                Performance.of(300, new BigDecimal("5.7"), 200),
                Amount.ofCents(60000L, Currency.CHF),
                Percentage.of(new BigDecimal("95")),
                Range.ofKm(440)
        );
    }

    @Nested
    class whenVehicleExists {

        @Test
        void savesVehicleWithUpdatedValues() {
            given(persistancePort.findById(validVehicleId()))
                    .willReturn(Optional.of(existingVehicle()));

            service.update(validVehicleId(), validSpecification(), validBatterySpecification(),
                    validMonthlyLeasePrice(), validNewStateOfHealth(), validNewMeasuredRange());

            ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
            then(persistancePort).should().save(captor.capture());
            Vehicle saved = captor.getValue();
            assertThat(saved.getVehicleSpecification()).isEqualTo(validSpecification());
            assertThat(saved.getBatterySpecification()).isEqualTo(validBatterySpecification());
            assertThat(saved.getMonthlyLeasePrice()).isEqualTo(validMonthlyLeasePrice());
            assertThat(saved.getLatestStateOfHealth()).isEqualTo(validNewStateOfHealth());
            assertThat(saved.getLatestMeasuredRangeKm()).isEqualTo(validNewMeasuredRange());
        }
    }

    @Nested
    class whenVehicleNotFound {

        @Test
        void throwsIllegalArgumentException() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(
                    validVehicleId(), validSpecification(), validBatterySpecification(),
                    validMonthlyLeasePrice(), validNewStateOfHealth(), validNewMeasuredRange()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void doesNotSaveWhenVehicleNotFound() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(
                    validVehicleId(), validSpecification(), validBatterySpecification(),
                    validMonthlyLeasePrice(), validNewStateOfHealth(), validNewMeasuredRange()))
                    .isInstanceOf(IllegalArgumentException.class);

            then(persistancePort).should(never()).save(any());
        }
    }
}
