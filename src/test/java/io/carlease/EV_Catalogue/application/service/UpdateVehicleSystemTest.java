package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateVehicleSystemTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VehiclePersistancePort vehiclePersistancePort;

    @Test
    @Sql("UpdateVehicleSystemTest.sql")
    void updateVehicle_persistsUpdatedStateAndReturnsRedirect() {
        URI url = UriComponentsBuilder.fromPath("/ev-catalogue/update_vehicle")
                .queryParam("vin",                         "WBA00000000000001")
                .queryParam("brand",                       "Tesla")
                .queryParam("model",                       "Model Y")
                .queryParam("range_km",                    500)
                .queryParam("vehicle_type",                "SEDAN")
                .queryParam("consumption_kwh_per_100km",   15.0)
                .queryParam("battery_capacity_kwh",        100.0)
                .queryParam("battery_voltage_v",           450.0)
                .queryParam("battery_max_charge_speed_kw", 15.0)
                .queryParam("monthly_lease_price_cents",   80000)
                .queryParam("currency",                    "CHF")
                .queryParam("new_state_of_health",         90.0)
                .queryParam("new_measured_range_km",       420)
                .build()
                .encode()
                .toUri();

        ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.POST, null, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        Vehicle updated = vehiclePersistancePort.findById(
                VehicleId.of("WBA00000000000001")).orElseThrow();
        assertThat(updated.getVehicleSpecification().getBrand()).isEqualTo("Tesla");
        assertThat(updated.getVehicleSpecification().getModel()).isEqualTo("Model Y");
        assertThat(updated.getVehicleSpecification().getRangeKm()).isEqualTo(Range.ofKm(500));
        assertThat(updated.getVehicleSpecification().getVehicleType()).isEqualTo(VehicleType.SEDAN);
        assertThat(updated.getVehicleSpecification().getConsumptionKwhPer100km())
                .isEqualTo(EnergyConsumption.of(new BigDecimal("15")));
        assertThat(updated.getBatterySpecification().getCapacityKwh()).isEqualByComparingTo(new BigDecimal("100"));
        assertThat(updated.getBatterySpecification().getVoltageV()).isEqualByComparingTo(new BigDecimal("450"));
        assertThat(updated.getBatterySpecification().getMaxChargeSpeedKw()).isEqualByComparingTo(new BigDecimal("15"));
        assertThat(updated.getMonthlyLeasePrice()).isEqualTo(Amount.ofCents(80000L, Currency.CHF));
        assertThat(updated.getLatestStateOfHealth()).isEqualTo(Percentage.of(new BigDecimal("90")));
        assertThat(updated.getLatestMeasuredRangeKm()).isEqualTo(Range.ofKm(420));
    }
}
