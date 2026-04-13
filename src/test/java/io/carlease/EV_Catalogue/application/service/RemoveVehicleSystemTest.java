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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveVehicleSystemTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VehiclePersistancePort vehiclePersistancePort;

    @Test
    @Sql("RemoveVehicleSystemTest.sql")
    void removeVehicle_deletesVehicleAndReturnsRedirect() {
        URI url = UriComponentsBuilder.fromPath("/ev-catalogue/remove_vehicle")
                .queryParam("vin", "WBA00000000000030")
                .build()
                .encode()
                .toUri();

        ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.POST, null, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(vehiclePersistancePort.existsById(
                VehicleId.of("WBA00000000000030"))).isFalse();
    }
}
