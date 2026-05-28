package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.VehicleId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AddVehicleSystemTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VehiclePersistencePort vehiclePersistencePort;

    @Test
    void addVehicle_persistsVehicleAndReturns201() {
        String body = """
                {
                  "vin": "WBA12345678901234",
                  "brand": "BMW",
                  "model": "iX3",
                  "rangeKm": 460,
                  "vehicleType": "SUV",
                  "consumptionKwhPer100km": 17.0,
                  "capacityKwh": 80.0,
                  "voltageV": 400.0,
                  "maxChargeSpeedKw": 11.0,
                  "horsePowerHp": 286,
                  "accelerationSec": 6.8,
                  "topSpeedKmh": 180,
                  "monthlyLeasePriceCents": 75000,
                  "currency": "CHF",
                  "initialStateOfHealth": 95.0,
                  "initialMeasuredRangeKm": 440
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/api/vehicles", new HttpEntity<>(body, headers), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(vehiclePersistencePort.existsById(VehicleId.of("WBA12345678901234"))).isTrue();
    }
}
