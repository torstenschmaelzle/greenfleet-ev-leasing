package io.carlease.sales.application.service;

import io.carlease.EV_Catalogue.adapter.out.persistence.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReserveVehiclesSystemTest {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private VehicleRepository vehicleRepository;

    @Test
    @Sql("ReserveVehiclesSystemTest.sql")
    void reserveVehicles_locksVehiclesInEVCatalogue() {
        String body = """
                { "vins": ["WBA00000000000211", "WBA00000000000212"] }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/api/sales/fleet-contracts/reserve",
                new HttpEntity<>(body, headers),
                Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(vehicleRepository.findById("WBA00000000000211").orElseThrow().getStatus()).isEqualTo("RESERVED");
        assertThat(vehicleRepository.findById("WBA00000000000212").orElseThrow().getStatus()).isEqualTo("RESERVED");
    }
}
