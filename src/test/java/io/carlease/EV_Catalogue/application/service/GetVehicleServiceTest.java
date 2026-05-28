package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.*;
import io.carlease.shared.domain.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class GetVehicleServiceTest {

    private final VehiclePersistancePort persistancePort = mock(VehiclePersistancePort.class);
    private final GetVehicleService service = new GetVehicleService(persistancePort);

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

    @Nested
    class whenVehicleExists {

        @Test
        void returnsVehicle() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.of(existingVehicle()));

            Vehicle result = service.getByVin(validVehicleId());

            assertThat(result.getVehicleId()).isEqualTo(validVehicleId());
        }
    }

    @Nested
    class whenVehicleNotFound {

        @Test
        void throwsVehicleNotFoundException() {
            given(persistancePort.findById(validVehicleId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.getByVin(validVehicleId()))
                    .isInstanceOf(VehicleNotFoundException.class);
        }
    }
}
