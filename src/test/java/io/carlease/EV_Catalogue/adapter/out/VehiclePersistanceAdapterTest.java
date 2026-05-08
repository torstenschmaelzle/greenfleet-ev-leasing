package io.carlease.EV_Catalogue.adapter.out;

import io.carlease.EV_Catalogue.adapter.out.persistence.VehicleRepository;
import io.carlease.EV_Catalogue.domain.*;
import io.carlease.shared.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(VehiclePersistanceAdapter.class)
class VehiclePersistanceAdapterTest {

    @Autowired
    private VehiclePersistanceAdapter adapter;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void savesVehicle() {
        Vehicle vehicle = VehicleFactory.createVehicle(
                VehicleId.of("WBA12345678901234"),
                VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                        EnergyConsumption.of(new BigDecimal("17"))),
                BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11")),
                Performance.of(286, new BigDecimal("6.8"), 180),
                Amount.ofCents(75000L, Currency.CHF),
                Percentage.of(new BigDecimal("95")),
                Range.ofKm(440)
        );

        adapter.save(vehicle);

        assertThat(vehicleRepository.count()).isEqualTo(1);
    }

    @Test
    void restoreVehicle() {
        Vehicle vehicle = VehicleFactory.createVehicle(
                VehicleId.of("WBA12345678901234"),
                VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                        EnergyConsumption.of(new BigDecimal("17"))),
                BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11")),
                Performance.of(286, new BigDecimal("6.8"), 180),
                Amount.ofCents(75000L, Currency.CHF),
                Percentage.of(new BigDecimal("95")),
                Range.ofKm(440)
        );
        adapter.save(vehicle);

        Vehicle restored = adapter.findById(VehicleId.of("WBA12345678901234")).orElseThrow();

        assertThat(restored.getVehicleId()).isEqualTo(VehicleId.of("WBA12345678901234"));
        assertThat(restored.getVehicleSpecification().getBrand()).isEqualTo("BMW");
        assertThat(restored.getMonthlyLeasePrice()).isEqualTo(Amount.ofCents(75000L, Currency.CHF));
        assertThat(restored.getLatestStateOfHealth()).isEqualTo(Percentage.of(new BigDecimal("95")));
        assertThat(restored.getLatestMeasuredRangeKm()).isEqualTo(Range.ofKm(440));



    }




    @Test
    @Sql("VehiclePersistanceAdapterTest.sql")
    void loadsVehicleById() {
        Vehicle vehicle = adapter.findById(VehicleId.of("WBA12345678901234")).orElseThrow();

        assertThat(vehicle.getVehicleId()).isEqualTo(VehicleId.of("WBA12345678901234"));
        assertThat(vehicle.getVehicleSpecification().getBrand()).isEqualTo("BMW");
        assertThat(vehicle.getVehicleSpecification().getModel()).isEqualTo("iX3");
        assertThat(vehicle.getVehicleSpecification().getVehicleType()).isEqualTo(VehicleType.SUV);
        assertThat(vehicle.getMonthlyLeasePrice()).isEqualTo(Amount.ofCents(75000L, Currency.CHF));
        assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
        assertThat(vehicle.getLatestStateOfHealth()).isEqualTo(Percentage.of(new BigDecimal("95")));
        assertThat(vehicle.getLatestMeasuredRangeKm()).isEqualTo(Range.ofKm(440));
    }

    @Test
    @Sql("VehiclePersistanceAdapterTest.sql")
    void existsByIdReturnsTrueForPersistedVehicle() {
        assertThat(adapter.existsById(VehicleId.of("WBA12345678901234"))).isTrue();
    }

    @Test
    void existsByIdReturnsFalseWhenVehicleIsNotPresent() {
        assertThat(adapter.existsById(VehicleId.of("WBA99999999999999"))).isFalse();
    }

    @Test
    @Sql("VehiclePersistanceAdapterTest.sql")
    void deletesVehicleById() {
        adapter.deleteById(VehicleId.of("WBA12345678901234"));

        assertThat(vehicleRepository.count()).isEqualTo(0);
    }

    @Test
    @Sql("VehiclePersistanceAdapterTest.sql")
    void savesUpdatedVehicleState() {
        Vehicle vehicle = adapter.findById(VehicleId.of("WBA12345678901234")).orElseThrow();

        vehicle.updateVehicleSpecification(VehicleSpecification.of("Tesla", "Model Y", Range.ofKm(500),
                VehicleType.SEDAN, EnergyConsumption.of(new BigDecimal("15"))));
        vehicle.updateBatterySpecification(BatterySpecification.of(
                new BigDecimal("100"), new BigDecimal("450"), new BigDecimal("15")));
        vehicle.updateMonthlyLeasePrice(Amount.ofCents(80000L, Currency.CHF));
        vehicle.updateBatteryHealthMeasurement(
                Percentage.of(new BigDecimal("90")), Range.ofKm(420));

        adapter.save(vehicle);

        Vehicle updated = adapter.findById(VehicleId.of("WBA12345678901234")).orElseThrow();
        assertThat(updated.getVehicleSpecification()).isEqualTo(
                VehicleSpecification.of("Tesla", "Model Y", Range.ofKm(500),
                        VehicleType.SEDAN, EnergyConsumption.of(new BigDecimal("15"))));
        assertThat(updated.getBatterySpecification()).isEqualTo(
                BatterySpecification.of(new BigDecimal("100"), new BigDecimal("450"), new BigDecimal("15")));
        assertThat(updated.getMonthlyLeasePrice()).isEqualTo(Amount.ofCents(80000L, Currency.CHF));
        assertThat(updated.getLatestStateOfHealth()).isEqualTo(Percentage.of(new BigDecimal("90")));
        assertThat(updated.getLatestMeasuredRangeKm()).isEqualTo(Range.ofKm(420));
    }
}
