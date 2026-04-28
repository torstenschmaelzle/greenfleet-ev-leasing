package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.*;
import io.carlease.shared.domain.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class AddVehicleServiceTest {

    private final VehiclePersistancePort persistancePort = mock(VehiclePersistancePort.class);
    private final AddVehicleService service = new AddVehicleService(persistancePort);

    // -- domain object helpers --

    private VehicleId validVehicleId() {
        return VehicleId.of("WBA12345678654234");
    }

    private VehicleSpecification validSpecification() {
        return VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV, EnergyConsumption.of(new BigDecimal("17")));
    }

    private BatterySpecification validBatterySpecification() {
        return BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11"));
    }

    private Performance validPerformance() {
        return Performance.of(286, new BigDecimal("6.8"), 180);
    }

    private Amount validMonthlyLeasePrice() {
        return Amount.ofCents(75000L, Currency.CHF);
    }

    private Percentage validInitialStateOfHealth() {
        return Percentage.of(new BigDecimal("95"));
    }

    private Range validInitialMeasuredRange() {
        return Range.ofKm(440);
    }

    // -- tests --

    @Nested
    class whenVehicleIsNew {

        @Test
        void savesVehicleSuccessfullyWhenVinIsNotPresent() {
            // given
            given(persistancePort.existsById(validVehicleId())).willReturn(false);

            // when
            service.add(validVehicleId(), validSpecification(), validBatterySpecification(),
                    validPerformance(), validMonthlyLeasePrice(),
                    validInitialStateOfHealth(), validInitialMeasuredRange());

            // then
            then(persistancePort).should().save(any(Vehicle.class));
        }
    }

    @Nested
    class whenVehicleAlreadyExists {

        @Test
        void throwsIllegalStateExceptionForDuplicateVin() {
            // given
            given(persistancePort.existsById(validVehicleId())).willReturn(true);

            // then
            assertThatThrownBy(() -> service.add(
                    validVehicleId(), validSpecification(), validBatterySpecification(),
                    validPerformance(), validMonthlyLeasePrice(),
                    validInitialStateOfHealth(), validInitialMeasuredRange()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void doesNotPersistVehicleForDuplicateVin() {
            // given
            given(persistancePort.existsById(validVehicleId())).willReturn(true);

            // when
            assertThatThrownBy(() -> service.add(
                    validVehicleId(), validSpecification(), validBatterySpecification(),
                    validPerformance(), validMonthlyLeasePrice(),
                    validInitialStateOfHealth(), validInitialMeasuredRange()))
                    .isInstanceOf(IllegalStateException.class);

            // then
            then(persistancePort).should(never()).save(any());
        }
    }

}
