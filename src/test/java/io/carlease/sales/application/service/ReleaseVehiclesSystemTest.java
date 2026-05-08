package io.carlease.sales.application.service;

import io.carlease.EV_Catalogue.adapter.out.persistence.VehicleRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReleaseVehiclesSystemTest {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private VehicleRepository vehicleRepository;

    @Test
    @Sql("ReleaseVehiclesSystemTest.sql")
    void releaseVehicles_makesVehiclesAvailableInEVCatalogue() {
        String body = """
                { "vins": ["WBA00000000000301", "WBA00000000000302"] }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/sales/fleet-contracts/reserve",
                HttpMethod.DELETE,
                new HttpEntity<>(body, headers),
                Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(vehicleRepository.findById("WBA00000000000301").orElseThrow().getStatus()).isEqualTo("AVAILABLE");
        assertThat(vehicleRepository.findById("WBA00000000000302").orElseThrow().getStatus()).isEqualTo("AVAILABLE");
    }
}
