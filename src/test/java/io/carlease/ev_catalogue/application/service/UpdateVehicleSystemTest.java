package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.*;
import io.carlease.shared.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateVehicleSystemTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VehiclePersistencePort vehiclePersistencePort;

    @Test
    @Sql("UpdateVehicleSystemTest.sql")
    void updateVehicle_persistsUpdatedStateAndReturns200() {
        String body = """
                {
                  "brand": "Tesla",
                  "model": "Model Y",
                  "rangeKm": 500,
                  "vehicleType": "SEDAN",
                  "consumptionKwhPer100km": 15.0,
                  "capacityKwh": 100.0,
                  "voltageV": 450.0,
                  "maxChargeSpeedKw": 15.0,
                  "monthlyLeasePriceCents": 80000,
                  "currency": "CHF",
                  "newStateOfHealth": 90.0,
                  "newMeasuredRangeKm": 420
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/vehicles/WBA00000000000001",
                HttpMethod.PUT,
                new HttpEntity<>(body, headers),
                Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Vehicle updated = vehiclePersistencePort.findById(
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
