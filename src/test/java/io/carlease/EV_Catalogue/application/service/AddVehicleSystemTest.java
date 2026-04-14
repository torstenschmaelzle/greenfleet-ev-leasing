package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.VehicleId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AddVehicleSystemTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VehiclePersistancePort vehiclePersistancePort;

    @Test
    void addVehicle_persistsVehicleAndReturnsRedirect() {
        URI url = UriComponentsBuilder.fromPath("/ev-catalogue/add_vehicle")
                .queryParam("vin",                         "WBA12345678901234")
                .queryParam("brand",                       "BMW")
                .queryParam("model",                       "iX3")
                .queryParam("range_km",                    460)
                .queryParam("vehicle_type",                "SUV")
                .queryParam("consumption_kwh_per_100km",   17.0)
                .queryParam("battery_capacity_kwh",        80.0)
                .queryParam("battery_voltage_v",           400.0)
                .queryParam("battery_max_charge_speed_kw", 11.0)
                .queryParam("horse_power_hp",              286)
                .queryParam("acceleration_sec",            6.8)
                .queryParam("top_speed_kmh",               180)
                .queryParam("monthly_lease_price_cents",   75000)
                .queryParam("currency",                    "CHF")
                .queryParam("initial_state_of_health",     95.0)
                .queryParam("initial_measured_range_km",   440)
                .build().encode().toUri();

        ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.POST, null, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(vehiclePersistancePort.existsById(
                VehicleId.of("WBA12345678901234"))).isTrue();
    }
}
