package io.carlease.EV_Catalogue.domain;

import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.VehicleType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class VehicleFactoryTest {
    //helper functions and test data builders

    private static final String VALID_VIN = "WBA12345678901234";

    private VehicleId vehicleId() {
        return VehicleId.of(VALID_VIN);
    }

    private VehicleSpecification specification() {
        return VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV, EnergyConsumption.of(new BigDecimal("17")));
    }

    private BatterySpecification batterySpec() {
        return BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11"));
    }

    private Performance performance() {
        return Performance.of(286, new BigDecimal("6.8"), 180);
    }

    private Amount monthlyPrice() {
        return Amount.ofCents(75000L, Currency.CHF);
    }

    private Vehicle freshVehicle() {
        return VehicleFactory.createVehicle(vehicleId(), specification(), batterySpec(), performance(), monthlyPrice(), Percentage.of(new BigDecimal("95")), Range.ofKm(440));
    }

    @Nested
    class createVehicleNullGuards {

        @Test
        void nullVehicleIdThrowsNullPointerException() {
            assertThatThrownBy(() -> VehicleFactory.createVehicle(
                    null, specification(), batterySpec(), performance(), monthlyPrice(),
                    Percentage.of(new BigDecimal("95")), Range.ofKm(440)))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void nullSpecificationThrowsNullPointerException() {
            assertThatThrownBy(() -> VehicleFactory.createVehicle(
                    vehicleId(), null, batterySpec(), performance(), monthlyPrice(),
                    Percentage.of(new BigDecimal("95")), Range.ofKm(440)))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void nullBatterySpecThrowsNullPointerException() {
            assertThatThrownBy(() -> VehicleFactory.createVehicle(
                    vehicleId(), specification(), null, performance(), monthlyPrice(),
                    Percentage.of(new BigDecimal("95")), Range.ofKm(440)))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void nullPerformanceThrowsNullPointerException() {
            assertThatThrownBy(() -> VehicleFactory.createVehicle(
                    vehicleId(), specification(), batterySpec(), null, monthlyPrice(),
                    Percentage.of(new BigDecimal("95")), Range.ofKm(440)))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void nullMonthlyLeasePriceThrowsNullPointerException() {
            assertThatThrownBy(() -> VehicleFactory.createVehicle(
                    vehicleId(), specification(), batterySpec(), performance(), null,
                    Percentage.of(new BigDecimal("95")), Range.ofKm(440)))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void nullInitialStateOfHealthThrowsNullPointerException() {
            assertThatThrownBy(() -> VehicleFactory.createVehicle(
                    vehicleId(), specification(), batterySpec(), performance(), monthlyPrice(),
                    null, Range.ofKm(440)))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void nullInitialMeasuredRangeThrowsNullPointerException() {
            assertThatThrownBy(() -> VehicleFactory.createVehicle(
                    vehicleId(), specification(), batterySpec(), performance(), monthlyPrice(),
                    Percentage.of(new BigDecimal("95")), null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class restoreVehicleNullGuards {

        @Test
        void nullVehicleIdThrowsNullPointerException() {
            assertThatThrownBy(() -> VehicleFactory.restoreVehicle(
                    null, specification(), batterySpec(), performance(), monthlyPrice(),
                    List.of(Percentage.of(new BigDecimal("95"))), List.of(Range.ofKm(440))))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void nullSpecificationThrowsNullPointerException() {
            assertThatThrownBy(() -> VehicleFactory.restoreVehicle(
                    vehicleId(), null, batterySpec(), performance(), monthlyPrice(),
                    List.of(Percentage.of(new BigDecimal("95"))), List.of(Range.ofKm(440))))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void nullStatesOfHealthThrowsNullPointerException() {
            assertThatThrownBy(() -> VehicleFactory.restoreVehicle(
                    vehicleId(), specification(), batterySpec(), performance(), monthlyPrice(),
                    null, List.of(Range.ofKm(440))))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void nullMeasuredRangesThrowsNullPointerException() {
            assertThatThrownBy(() -> VehicleFactory.restoreVehicle(
                    vehicleId(), specification(), batterySpec(), performance(), monthlyPrice(),
                    List.of(Percentage.of(new BigDecimal("95"))), null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    void restorevehicle() {
        Vehicle vehicle = freshVehicle();
        Vehicle restored = VehicleFactory.restoreVehicle(
                vehicle.getVehicleId(),
                vehicle.getVehicleSpecification(),
                vehicle.getBatterySpecification(),
                vehicle.getPerformance(),
                vehicle.getMonthlyLeasePrice(),
                List.of(vehicle.getHealthMeasurements().get(0).getStateOfHealth()),
                List.of(vehicle.getHealthMeasurements().get(0).getMeasuredRangeKm())
        );
        assertThat(restored.getVehicleId()).isEqualTo(vehicle.getVehicleId());
        assertThat(restored.getVehicleSpecification()).isEqualTo(vehicle.getVehicleSpecification());
        assertThat(restored.getBatterySpecification()).isEqualTo(vehicle.getBatterySpecification());
        assertThat(restored.getPerformance()).isEqualTo(vehicle.getPerformance());
        assertThat(restored.getMonthlyLeasePrice()).isEqualTo(vehicle.getMonthlyLeasePrice());
        assertThat(restored.getHealthMeasurements()).hasSize(1);
        assertThat(restored.getHealthMeasurements().get(0).getStateOfHealth()).isEqualTo(vehicle.getHealthMeasurements().get(0).getStateOfHealth());
        assertThat(restored.getHealthMeasurements().get(0).getMeasuredRangeKm()).isEqualTo(vehicle.getHealthMeasurements().get(0).getMeasuredRangeKm());
    }

    @Test
    void restoreVehicle_withoutHealthMeasurements_throwsException() {
        Vehicle vehicle = freshVehicle();
        assertThatThrownBy(() -> VehicleFactory.restoreVehicle(
                vehicle.getVehicleId(),
                vehicle.getVehicleSpecification(),
                vehicle.getBatterySpecification(),
                vehicle.getPerformance(),
                vehicle.getMonthlyLeasePrice(),
                List.of(),
                List.of()
        )).isInstanceOf(InvalidVehicleAttributeException.class)
                .hasMessageContaining("initialHealthMeasurements");
    }
}
